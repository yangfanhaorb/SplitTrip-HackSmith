package edu.amherst.fyang17.hacksmithbackend;

/**
 * Created by Administrator on 3/28/2015.
 */
import java.util.*;
public class ImportantFunctions {
    public static void buildPersonList(String[] people){
        if (Persons.count(Persons.class,null,null)!=0) {
            Persons.deleteAll(Persons.class);
        }
        int n = people.length;
        for (int i=0;i<n;i++){
            Persons temp = new Persons(people[i]);
            temp.save();
        }
    }
    public static void buildRelationTable(){
        if (TransactionTable.count(TransactionTable.class,null,null)!=0) {
            TransactionTable.deleteAll(TransactionTable.class);
        }
        if (RelationTable.count(RelationTable.class,null,null)!=0) {
            RelationTable.deleteAll(RelationTable.class);
        }
        List<Persons> people = Persons.listAll(Persons.class);
        int k = people.size();
        for (int i=0;i<k;i++){
            for (int j=0;j<k;j++){
                if (i!=j){
                    RelationTable temp = new RelationTable(people.get(i).name,people.get(j).name,0);
                    temp.save();
                }
            }
        }
    }

    public static void addTransaction(String payer, String payee, float amount, String description, String currency){
        TransactionTable temp = new TransactionTable(payer,payee,amount,description,currency);
        temp.save();
        String[] payees = payee.split(",");
        float fraction = amount/payees.length;
        if (currency.equals("USD"))
            fraction = fraction/1;
        else if (currency.equals("CNY"))
            fraction = fraction/(float)6.21;
        else if (currency.equals("PEN"))
            fraction = fraction/(float)3.15;
        List<Persons> a = Persons.find(Persons.class,"name=?",payer);
        List<RelationTable> b = RelationTable.find(RelationTable.class,"p1=?",a.get(0).name);
        for (int i=0;i<b.size();i++){
            boolean paid = false;
            for (int j=0;j<payees.length;j++){
                if (b.get(i).p2.equals(payees[j])){
                    paid = true;
                }
            }
            if (paid==true){
                b.get(i).amount = b.get(i).amount + fraction;
                b.get(i).save();
            }
        }
        for (int i=0;i<payees.length;i++) {
            List<Persons> c = Persons.find(Persons.class, "name=?", payees[i]);
            List<RelationTable> d = RelationTable.find(RelationTable.class, "p1=?", c.get(0).name);
            for (int j=0; j<d.size(); j++){
                if (d.get(j).p2.equals(payer)){
                    d.get(j).amount=d.get(j).amount-fraction;
                    d.get(j).save();
                    break;
                }
            }
        }
    }

    //This function returns the list of all historical transactions and feeds it to the Payment History (TransactionList) activity
    public static ArrayList<Items> returnList(){
        List<TransactionTable> list = TransactionTable.listAll(TransactionTable.class);
        ArrayList<Items> toReturn  = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            String[] payees = list.get(i).payees.split(",");
            String s1 = list.get(i).payer;
            String s2 = "For ";
            for (int j=0;j<payees.length;j++){
                s2 = s2+payees[j]+", ";
            }
            switch (list.get(i).currency){
                case "USD":
                    s1 = s1+" paid $"+list.get(i).amount;
                    break;
                case "CNY":
                    s1 = s1+" paid ￥"+list.get(i).amount;
                    break;
                case "PEN":
                    s1 = s1+" paid Sol."+list.get(i).amount;
                    break;
                default: s1 = s1+" paid $"+list.get(i).amount;
            }
            s2 = s2+list.get(i).description;
            toReturn.add(new Items(s1,s2));
        }
        return toReturn;
    }

    //This function returns the balance of everyone in the group and feeds this to the Personal Balances (TransactionList2) for display
    public static ArrayList<Items2> returnBalance(){
        ArrayList<Items2> toReturn = new ArrayList<>();
        List<Persons> people = Persons.listAll(Persons.class);
        int n = people.size();
        for (int i=0;i<n;i++) {
            List<RelationTable> list = RelationTable.find(RelationTable.class,"p1=?",people.get(i).name);
            float sum = 0;
            for (int j=0;j<n-1;j++){
                sum = sum + list.get(j).amount;
            }
            toReturn.add(new Items2(people.get(i).name,String.format("%.2f",sum)));
        }
        return toReturn;
    }

    //This function returns the details regarding how much each person owes or is owed by everyone else. This is displayed in the Details (details) activity
    public static ArrayList<Items> returnDetail(){
        ArrayList<Items> toReturn = new ArrayList<>();
        List<Persons> people = Persons.listAll(Persons.class);
        int n = people.size();
        for (int i=0;i<n;i++){
            List<RelationTable> list = RelationTable.find(RelationTable.class,"p1=?",people.get(i).name);
            String payee = "";
            for (int j=0;j<n-1;j++){
                payee=payee+list.get(j).p2+": "+String.format("%.2f",list.get(j).amount)+", ";
            }
            toReturn.add(new Items(people.get(i).name,payee));
        }
        return toReturn;
    }

    //This function returns the personal details regarding owing or being owed by other people
    public static ArrayList<Items> returnPersonalDues(String personName){
        ArrayList<Items> toReturn = new ArrayList<>();
        List<Persons> people = Persons.listAll(Persons.class);
        List<RelationTable> list = RelationTable.find(RelationTable.class,"p1=?",personName);
        for (int i=0;i<people.size()-1;i++){
            String money = String.format("%.2f",Math.abs(list.get(i).amount));
            String description;
            if (list.get(i).amount>=0){
                description = personName+" is owed by "+list.get(i).p2;
            }
            else description = personName+" owes "+list.get(i).p2;
            toReturn.add(new Items("$"+money,description));
        }
        return toReturn;
    }
}
