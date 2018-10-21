package mail;

import java.text.NumberFormat;

public class PublishedPurchaseRequest extends MailerService {

    public PublishedPurchaseRequest(double totalAmount, String urlPurchaseRequest) {
        super();

        String displayTotalAmount = NumberFormat.getCurrencyInstance().format(totalAmount);
        
        this.context.put("totalAmount", displayTotalAmount);
        this.context.put("urlPurchaseRequest", urlPurchaseRequest);
    }

    @Override
    public void send() {
        this.from = "noreply@smartsearch.com.br";
        this.subject = "OPORTUNIDADE | Pedido de compra";

        this.template = "publishedPurchaseRequest";
        this.context.put("shortName", "SmartSearch");

        this.mail.sendHTML(this);
    }
}
