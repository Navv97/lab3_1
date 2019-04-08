package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    @Test
    public void testIssuanceWithOnePositionShouldReturnInvoiceWithOnePosition(){
        ProductData productData = Mockito.mock(ProductData.class);
        TaxPolicy taxPolicy = Mockito.mock(TaxPolicy.class);
        InvoiceRequest invoiceRequest = new InvoiceRequest(new ClientData(new Id("1"),"Bob"));
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        Mockito.when(taxPolicy.calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class))).thenReturn(new Tax(new Money(1.99), "Tax Description"));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size(), is(equalTo(1)));
        Mockito.verify(taxPolicy, Mockito.times(1)).calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class));
    }

    @Test
    public void testIssuanceWithTwoPositionsShouldReturnInvoiceWithTwoPositions(){
        ProductData productData = Mockito.mock(ProductData.class);
        TaxPolicy taxPolicy = Mockito.mock(TaxPolicy.class);
        InvoiceRequest invoiceRequest = new InvoiceRequest(new ClientData(new Id("1"),"Bob"));
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        Mockito.when(taxPolicy.calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class))).thenReturn(new Tax(new Money(1.99), "Tax Description"));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size(), is(equalTo(2)));
        Mockito.verify(taxPolicy, Mockito.times(2)).calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class));
    }

    @Test
    public void testIssuanceWithThreePositionsShouldCallGetTypeThreeTimes(){
        ProductData productData = Mockito.mock(ProductData.class);
        TaxPolicy taxPolicy = Mockito.mock(TaxPolicy.class);
        InvoiceRequest invoiceRequest = new InvoiceRequest(new ClientData(new Id("1"),"Bob"));
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        Mockito.when(taxPolicy.calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class))).thenReturn(new Tax(new Money(1.99), "Tax Description"));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        Mockito.verify(productData, Mockito.times(3)).getType();
    }

    @Test
    public void testIssuanceWithManyPositionsShouldCreateInvoiceOnce(){
        ProductData productData = Mockito.mock(ProductData.class);
        TaxPolicy taxPolicy = Mockito.mock(TaxPolicy.class);
        InvoiceRequest invoiceRequest = new InvoiceRequest(new ClientData(new Id("1"),"Bob"));
        InvoiceFactory invoiceFactorySpy = Mockito.spy(new InvoiceFactory());
        BookKeeper bookKeeper = new BookKeeper(invoiceFactorySpy);
        Mockito.when(taxPolicy.calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class))).thenReturn(new Tax(new Money(1.99), "Tax Description"));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        Mockito.verify(invoiceFactorySpy, Mockito.times(1)).create(Matchers.any(ClientData.class));
    }

    @Test
    public void testIssuanceWithThreePositionsShouldCallCalculateTaxThreeTimes(){
        ProductData productData = Mockito.mock(ProductData.class);
        TaxPolicy taxPolicy = Mockito.mock(TaxPolicy.class);
        InvoiceRequest invoiceRequest = new InvoiceRequest(new ClientData(new Id("1"),"Bob"));
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        Mockito.when(taxPolicy.calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class))).thenReturn(new Tax(new Money(1.99), "Tax Description"));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        Mockito.verify(taxPolicy, Mockito.times(3)).calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class));
    }

    

}