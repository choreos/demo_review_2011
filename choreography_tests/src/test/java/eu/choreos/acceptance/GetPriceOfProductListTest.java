package eu.choreos.acceptance;

import static eu.choreos.utils.AcceptanceTestUtils.formatEndpoint;
import static eu.choreos.utils.AcceptanceTestUtils.registerSupermarkets;
import static eu.choreos.utils.AcceptanceTestUtils.removeSupermarkets;
import static eu.choreos.utils.ManualEnactment.getChoreography;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.choreos.vv.abstractor.Choreography;
import eu.choreos.vv.abstractor.Service;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class GetPriceOfProductListTest {

	private static WSClient customerClient;
	private static Service customer;
	private static Choreography futureMarket;

	
	@BeforeClass
	public static void setUpSupermarketsAndCustomerWSClient() throws Exception {
		 futureMarket = getChoreography();
		registerSupermarkets();
		customer = futureMarket.getServicesForRole("customer").get(0);
		customerClient = new WSClient(customer.getWSDL());
		customerClient.setEndpoint(formatEndpoint(customer.getWSDL()));
	}

	@AfterClass
	public static void removeRegisteredSupermarkets() throws Exception {
		Service smregistry = customer.getServicesForRole("customer").get(0);
		removeSupermarkets(smregistry);
	}

	@Test
	public void shouldReturnTheLowerPriceOfAProduct() throws Exception {
		Item list = new ItemImpl("getPriceOfProductListRequest");
		Item item1 = new ItemImpl("item");
		item1.setContent("product1");
		list.addChild(item1);

		Item response = customerClient.request("getPriceOfProductList", list);
		Double actualPrice = response.getChild("order").getChild("price")
				.getContentAsDouble();

		assertTrue(actualPrice.toString(),
				(actualPrice > 0.9 && actualPrice < 2.0));
	}
	
	@Test
	public void shouldReturnTheCorrectPriceOfAListWithTwoItems() throws Exception {
		Item list = new ItemImpl("getPriceOfProductListRequest");
		
		Item item1 = new ItemImpl("item");
		item1.setContent("product1");
		list.addChild(item1);
		
		Item item2 = new ItemImpl("item");
		item2.setContent("product2");
		list.addChild(item2);
		
		
		Item response = customerClient.request("getPriceOfProductList", list);
		Double actualPrice = response.getChild("order").getChild("price").getContentAsDouble();
		
		Double minPrice = new Double(2.9);
		Double maxPrice = new Double(4.9);
		
		assertTrue(actualPrice.toString(), (actualPrice > minPrice && actualPrice < maxPrice));	
	}
	
	@Test
	public void shouldReturnAnOrderID() throws Exception {
		Item list = new ItemImpl("getPriceOfProductListRequest");
		
		Item item1 = new ItemImpl("item");
		item1.setContent("product1");
		list.addChild(item1);
		
		Item response = customerClient.request("getPriceOfProductList", list);
		
		String id = response.getChild("order").getChild("id").getContent();
		
		assertTrue(Integer.parseInt(id) > 0);	
	}

}
