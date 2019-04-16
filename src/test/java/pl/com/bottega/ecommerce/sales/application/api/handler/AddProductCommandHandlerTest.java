package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;
import pl.com.bottega.ecommerce.system.application.SystemUser;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext;
    private AddProductCommandHandler addProductCommandHandler;
    private AddProductCommand addProductCommand;
    private Client client;
    private Reservation reservation;
    private Product product;

    @Before
    public void init(){
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
        suggestionService = mock(SuggestionService.class);
        clientRepository = mock(ClientRepository.class);
        systemContext = mock(SystemContext.class);
        addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository, suggestionService, clientRepository, systemContext);
        addProductCommand = new AddProductCommand(Id.generate(), Id.generate(), 1);
        reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED, new ClientData(Id.generate(), "Client Data"),new Date());
        product = new Product(Id.generate(), new Money(1), "Standard Product", ProductType.STANDARD);
        when(productRepository.load(addProductCommand.getProductId())).thenReturn(product);
        when(reservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);
        when(productRepository.load(addProductCommand.getProductId())).thenReturn(product);
        when(reservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);
    }

    @Test
    public void testHandleShouldReturnOneReservation() {
        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository, times(1)).load(any(Id.class));
    }

    @Test
    public void testHandleShouldCallLoadProductOnce() {
        addProductCommandHandler.handle(addProductCommand);

        verify(productRepository, times(1)).load(addProductCommand.getProductId());
    }

}