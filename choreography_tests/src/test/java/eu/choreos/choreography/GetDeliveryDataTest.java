package eu.choreos.choreography;

import static eu.choreos.choreography.AcceptanceTestUtils.*;
import static eu.choreos.choreography.ManualEnactment.futureMarket;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.choreos.vv.abstractor.Service;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class GetDeliveryDataTest {
	
	private static String purchaseID;
	private static String shipperName;
	private static Service customer;
	
	@BeforeClass
	public static void setUpSupermarketsAndCustomerWSClientAndPurchaseProduct()
			throws Exception {
		registerSupermarkets();
		customer = futureMarket.getServicesForRole("customer").get(0);
		
		purchaseID = requestIdOfSimpleOrder(customer);
		shipperName = purchaseProduct(customer, purchaseID);
	}
	
	@AfterClass
	public static void removeSupermarketsFromRegistry() throws Exception {
		Service smregistry = customer.getServicesForRole("customer").get(0);
		removeSupermarkets(smregistry);
	}

	@Test
	public void shouldGetTheDeliveryDataOfAPurchase() throws Exception {
		Item request = new ItemImpl("getDeliveryData");
		Item shipper = new ItemImpl("shipper");
		shipper.setContent(shipperName);
		request.addChild(shipper);

		Item id = new ItemImpl("orderID");
		id.setContent(purchaseID);
		request.addChild(id);
		
		WSClient customerClient = new WSClient(customer.getWSDL());
		customerClient.setEndpoint(formatEndpoint(customer.getWSDL()));
		Item response = customerClient.request("getDeliveryData", request);
		
		assertEquals("Sat Dec 24 20:15:00 BRST 2011", response.getChild("delivery").getContent());
	}

}
