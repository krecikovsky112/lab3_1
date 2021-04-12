package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Mock
    private ProductData productData2;

    @Mock
    ProductData productData3;

    private ClientData client;
    private InvoiceRequest request;
    private Invoice invoice;

    @BeforeEach
    void setUp() {
        bookKeeper = new BookKeeper(factory);
        productData = mock(ProductData.class);
        taxPolicy = mock(TaxPolicy.class);
        client = new ClientData(Id.generate(), "Andrzej");
        productData2 = mock(ProductData.class);
        request = new InvoiceRequest(client);
        invoice = new Invoice(Id.generate(), client);
        productData3 = mock(ProductData.class);
    }

    @Test
    void shouldReturnIvoiceWithOnePositon() {
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(BigDecimal.ONE), "tax"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem item = new RequestItem(productData, 1, new Money(3));
        request.add(item);

        when(factory.create(client)).thenReturn(invoice);

        bookKeeper.issuance(request, taxPolicy);
        assertEquals(invoice.getItems().size(), 1);
    }

    @Test
    void shouldCallCalculateTaxTwice() {
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(BigDecimal.ONE), "tax"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);
        when(productData2.getType()).thenReturn(ProductType.STANDARD);

        RequestItem item = new RequestItem(productData, 1, new Money(3));
        request.add(item);

        RequestItem item2 = new RequestItem(productData2, 3, new Money(5));
        request.add(item2);

        when(factory.create(client)).thenReturn(invoice);

        bookKeeper.issuance(request, taxPolicy);

        Mockito.verify(taxPolicy, times(2)).calculateTax(any(ProductType.class), any(Money.class));
    }

    @Test
    void shouldReturnInvoiceWithZeroPosition() {
        when(factory.create(client)).thenReturn(invoice);

        bookKeeper.issuance(request, taxPolicy);

        assertEquals(invoice.getItems().size(), 0);
    }

    @Test
    void shouldReturnInvoiceWithThreePosition() {
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(BigDecimal.ONE), "tax"));

        when(productData.getType()).thenReturn(ProductType.STANDARD);
        when(productData2.getType()).thenReturn(ProductType.FOOD);

        RequestItem item = new RequestItem(productData, 1, new Money(3));
        request.add(item);

        RequestItem item2 = new RequestItem(productData2, 3, new Money(5));
        request.add(item2);


        when(factory.create(client)).thenReturn(invoice);

        bookKeeper.issuance(request, taxPolicy);

        assertEquals(invoice.getItems().size(), 2);
    }
}


