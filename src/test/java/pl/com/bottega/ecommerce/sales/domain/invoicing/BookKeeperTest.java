package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    Product product;
    ProductData productData;
    TaxPolicy taxPolicy;
    InvoiceRequest invoiceRequest;
    BookKeeper bookKeeper;

    @Before
    public void init(){
        product = new Product(new Id("1"),new Money(21.37),"Peanut butter", ProductType.FOOD);
        productData = product.generateSnapshot();
        taxPolicy = Mockito.mock(TaxPolicy.class);
        invoiceRequest = new InvoiceRequest(new ClientData(new Id("1"),"Bob"));
        bookKeeper = new BookKeeper(new InvoiceFactory());
        Mockito.when(taxPolicy.calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class))).thenReturn(new Tax(new Money(1.99), "Tax Description"));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
    }

    @Test
    public void testIssuanceWithOnePositionShouldReturnInvoiceWithOnePosition(){
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size(), is(equalTo(1)));
        Mockito.verify(taxPolicy, Mockito.times(1)).calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class));
    }

    @Test
    public void testIssuanceWithTwoPositionsShouldReturnInvoiceWithTwoPositions(){
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size(), is(equalTo(2)));
        Mockito.verify(taxPolicy, Mockito.times(2)).calculateTax(Matchers.any(ProductType.class), Matchers.any(Money.class));
    }

    public void addItems(){
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
        invoiceRequest.add(new RequestItem(productData,1, new Money(1.99)));
    }

    @Test
    public void testIssuanceWithManyPositionsShouldCreateInvoiceOnce(){
        InvoiceFactory invoiceFactorySpy = Mockito.spy(new InvoiceFactory());
        bookKeeper = new BookKeeper(invoiceFactorySpy);
        addItems();
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        Mockito.verify(invoiceFactorySpy, Mockito.times(1)).create(Matchers.any(ClientData.class));
    }

    @Test
    public void testIssuanceWithThreePositionsShouldCallCalculateTaxThreeTimes(){
        addItems();
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        Mockito.verify(taxPolicy, Mockito.times(3)).calculateTax(Matchers.any(ProductType.class),Matchers.any(Money.class));
    }

    @Test
    public void testTotalCostOfInvoice(){
        addItems();
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(new Money(5.97), is(equalTo(invoice.getNet())));
    }

    @Test
    public void testInvoiceRequestShouldHaveClientData() {
        addItems();
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getClient(), equalTo(invoiceRequest.getClient()));
    }

}
