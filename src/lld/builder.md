```java
abstract class Payment {

    protected UpiPayment upiPayment;
    protected CreditCardPayment creditCardPayment;
    protected DebitCardPayment debitCardPayment;

    public Payment(UpiPayment upi,
                   CreditCardPayment credit,
                   DebitCardPayment debit) {
        this.upiPayment = upi;
        this.creditCardPayment = credit;
        this.debitCardPayment = debit;
    }

    public void payUsingUpi(double amount) {
        if (upiPayment != null)
            upiPayment.pay(amount);
        else
            System.out.println("UPI payment not supported.");
    }

    public void payUsingCreditCard(double amount) {
        if (creditCardPayment != null)
            creditCardPayment.pay(amount);
        else
            System.out.println("Credit Card payment not supported.");
    }

    public void payUsingDebitCard(double amount) {
        if (debitCardPayment != null)
            debitCardPayment.pay(amount);
        else
            System.out.println("Debit Card payment not supported.");
    }

    public abstract void display();
}

// -------- Payment Types --------
class EcommercePayment extends Payment {

    public EcommercePayment(UpiPayment upi,
                            CreditCardPayment credit,
                            DebitCardPayment debit) {
        super(upi, credit, debit);
    }

    public void display() {
        System.out.println("E-commerce Payment");
    }
}

class BillPayment extends Payment {

    public BillPayment(UpiPayment upi,
                       CreditCardPayment credit,
                       DebitCardPayment debit) {
        super(upi, credit, debit);
    }

    public void display() {
        System.out.println("Bill Payment");
    }
}
```