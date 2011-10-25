package eu.choreos.acceptance.features;

import static eu.choreos.utils.AcceptanceTestUtils.formatEndpoint;
import static eu.choreos.utils.AcceptanceTestUtils.purchaseProduct;
import static eu.choreos.utils.AcceptanceTestUtils.registerSupermarkets;
import static eu.choreos.utils.AcceptanceTestUtils.removeSupermarkets;
import static eu.choreos.utils.AcceptanceTestUtils.requestIdOfSimpleOrder;
import static eu.choreos.utils.ManualEnactment.futureMarket;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.choreos.vv.abstractor.Service;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class GetDeliveryDataTest {
	private static DateFormat dtf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
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
		
		WSClient customerClient = customer.getWSClient();
		customerClient.setEndpoint(formatEndpoint(customer.getWSDL()));
		Item response = customerClient.request("getDeliveryData", request);
		
		assertTrue(response.getChild("delivery").getContent().contains("Sat Dec 24 20:15:00"));
	}

}
