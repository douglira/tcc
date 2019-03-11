package services.mail;

import java.text.NumberFormat;

public class RefusedSuggestedQuote extends MailerService {

    public RefusedSuggestedQuote(String buyerName, String refusedReason, int purchaseRequestId, double quoteTotalAmount, String urlPurchaseRequest) {
        super();

        String displayTotalAmount = NumberFormat.getCurrencyInstance().format(quoteTotalAmount);

        this.context.put("buyerName", buyerName);
        this.context.put("refusedReason", refusedReason);
        this.context.put("purchaseRequestId", purchaseRequestId);
        this.context.put("quoteTotalAmount", displayTotalAmount);
        this.context.put("urlPurchaseRequest", urlPurchaseRequest);
    }

    @Override
    public void send() {
        this.from = "noreply@smartsearch.com.br";
        this.subject = "COTAÇÂO | Sua cotação foi recusada :(";

        this.template = "refusedQuoteSuggested";
        this.context.put("shortName", "SmartSearch");

        this.mail.sendHTML(this);
    }
}
