package eu.choreos.choreography;

import static eu.choreos.choreography.AcceptanceTestUtils.registerSupermarkets;
import static eu.choreos.choreography.AcceptanceTestUtils.removeSupermarkets;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.choreos.vv.abstractor.Choreography;
import eu.choreos.vv.abstractor.Service;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.WSClient;
import static eu.choreos.choreography.ManualEnactment.getChoreography;;

public class RegisterSupermarketTest {
	private Service smregistry;
	private static Choreography futureMarket;
	
	@BeforeClass
	public static void enactChoreography(){
		 futureMarket = getChoreography();
	}
	
	@Before
	public void setUpSMRegistry() throws Exception {
		Service supermarket1 = futureMarket.getServicesForRole("supermarket").get(0);
		smregistry = supermarket1.getServicesForRole("supermarket").get(0);
	}
	
	@After
	public void removeRegisteredSupermarkets() throws Exception {
		removeSupermarkets(smregistry);
	}

	@Test
	public void shouldRegistrySupermarkets() throws Exception {
		registerSupermarkets();
		WSClient smRegistryClient = new WSClient(smregistry.getWSDL());
		smRegistryClient.setEndpoint(smregistry.getWSDL());
		
		Item response = smRegistryClient.request("getList", (Item) null);
		List<Item> list = response.getChildAsList("return");
		checkIfEndpointsExists(list);
	}
	
	private void checkIfEndpointsExists(List<Item> list) {
		List<Service> supermarkets = futureMarket.getServicesForRole("supermarket");
		
		for (Service service : supermarkets) {
			String wsdl = service.getWSDL();
			verifyIfEndpointExists(wsdl, list);
		}
	}

	private void verifyIfEndpointExists(String endpoint, List<Item> list) {
		for (Item item : list) {
			if(item.getContent().equals(endpoint))
				return;
		}
		fail("endpoint " + endpoint + " does not exists");
	}
}
