package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    @Mock
    private InvoiceFactory factory;

    @Mock
    private TaxPolicy taxPolicy;

    @Mock
    private BookKeeper bookKeeper;

    @Mock
    private ProductData productData;

    @BeforeEach
    void setUp(){
        bookKeeper=new BookKeeper(factory);
        productData=mock(ProductData.class);
        taxPolicy=mock(TaxPolicy.class);
    }

    @Test
    void shouldReturnIvoiceWithOnePositon()
    {
        ClientData client = new ClientData(Id.generate(),"Andrzej");
        InvoiceRequest request=new InvoiceRequest(client);

        when(taxPolicy.calculateTax(any(ProductType.class),any(Money.class))).thenReturn(new Tax(new Money(BigDecimal.ONE),"tax"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem item=new RequestItem(productData,1,new Money(3));
        request.add(item);

        Invoice invoice = new Invoice(Id.generate(),client);
        when(factory.create(client)).thenReturn(invoice);

        bookKeeper.issuance(request,taxPolicy);
        assertEquals(invoice.getItems().size(),1);
    }
}

