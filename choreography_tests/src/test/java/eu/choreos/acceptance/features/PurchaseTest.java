package eu.choreos.acceptance.features;

import static eu.choreos.utils.AcceptanceTestUtils.formatEndpoint;
import static eu.choreos.utils.AcceptanceTestUtils.getPersonalData;
import static eu.choreos.utils.AcceptanceTestUtils.registerSupermarkets;
import static eu.choreos.utils.AcceptanceTestUtils.requestIdOfSimpleOrder;
import static eu.choreos.utils.ManualEnactment.getChoreography;
import static org.junit.Assert.assertEquals;

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
		 customer = futureMarket.getServicesForRole("customer").get(0);
		Service smregistry = customer.getServicesForRole("customer").get(0);
		registerSupermarkets(smregistry);		
		customerClient = customer.getWSClient();
		customerClient.setEndpoint(formatEndpoint(customer.getWSDL()));
		purchaseID = requestIdOfSimpleOrder(customer);
		
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
