package eu.choreos.choreography;

import static eu.choreos.choreography.AcceptanceTestUtils.*;
import static eu.choreos.choreography.ManualEnactment.getChoreography;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.choreos.vv.abstractor.Choreography;
import eu.choreos.vv.abstractor.Service;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class PurchaseTest {

	private static WSClient customerClient;
	private static Service customer;
	private static String purchaseID;
	private static Choreography futureMarket;

	@BeforeClass
	public static void setUpSupermarketsAndCustomerWSClientAndRequestProductList()
			throws Exception {
		 futureMarket = getChoreography();
		registerSupermarkets();		
		customer = futureMarket.getServicesForRole("customer").get(0);
		customerClient = new WSClient(customer.getWSDL());
		customerClient.setEndpoint(formatEndpoint(customer.getWSDL()));
		purchaseID = requestIdOfSimpleOrder(customer);
		
	}
	
	@AfterClass
	public static void removeSupermarketsFromRegistry() throws Exception {
		Service smregistry = customer.getServicesForRole("customer").get(0);
		removeSupermarkets(smregistry);
	}

	@Test
	public void shouldReturnTheShipperEndpointWhenProductsIsPurchased()
			throws Exception {

		Item request = new ItemImpl("purchase");
		Item orderID = new ItemImpl("id");
		orderID.setContent(purchaseID);
		request.addChild(orderID);

		Item personalData = getPersonalData();
		request.addChild(personalData);

		Item response = customerClient.request("purchase", request);

		assertEquals("Shipper1", response.getChild("out").getContent());
	}

}
