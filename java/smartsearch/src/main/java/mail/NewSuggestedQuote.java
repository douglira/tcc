package mail;

import java.text.NumberFormat;

public class NewSuggestedQuote extends MailerService {

    public NewSuggestedQuote(String corporateName, int purchaseRequestId, double totalAmount, String urlQuote) {
        super();

        String displayTotalAmount = NumberFormat.getCurrencyInstance().format(totalAmount);

        this.context.put("corporateName", corporateName);
        this.context.put("purchaseRequestId", purchaseRequestId);
        this.context.put("totalAmount", displayTotalAmount);
        this.context.put("urlQuote", urlQuote);
    }

    @Override
    public void send() {
        this.from = "noreply@smartsearch.com.br";
        this.subject = "OPORTUNIDADE | Nova Cotação";

        this.template = "newQuoteSuggested";
        this.context.put("shortName", "SmartSearch");

        this.mail.sendHTML(this);
    }
}
