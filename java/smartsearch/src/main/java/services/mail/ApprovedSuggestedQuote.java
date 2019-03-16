package services.mail;

import java.text.NumberFormat;

public class ApprovedSuggestedQuote extends MailerService {

    public ApprovedSuggestedQuote(String buyerName, int orderId, double orderTotalAmount, String urlOrder) {
        super();

        String displayTotalAmount = NumberFormat.getCurrencyInstance().format(orderTotalAmount);

        this.context.put("buyerName", buyerName);
        this.context.put("orderId", orderId);
        this.context.put("orderTotalAmount", displayTotalAmount);
        this.context.put("urlOrder", urlOrder);
    }

    @Override
    public void send() {
        this.from = "noreply@smartsearch.com.br";
        this.subject = "COTAÇÃO | Parabéns sua cotação foi aprovada!!!";

        this.template = "approvedQuoteSuggested";
        this.context.put("shortName", "SmartSearch");

        this.mail.sendHTML(this);
    }
}
