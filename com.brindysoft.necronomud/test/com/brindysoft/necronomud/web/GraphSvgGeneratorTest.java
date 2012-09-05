package com.brindysoft.necronomud.web;

import static org.junit.Assert.assertEquals;

import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.necronomud.Place;

public class GraphSvgGeneratorTest {

	private GraphSvgGenerator testSubject;

	@Before
	public void setUp() {
		testSubject = new GraphSvgGenerator();
	}

	@Test
	public void testThreePlacesConnectedSouthAndWest() throws Exception {
		Place place1 = new Place(null, "0001", null);
		Place place2 = new Place(null, "0002", null);
		Place place3 = new Place(null, "0003", null);

		place1.connect(place2, "south", "north");
		place2.connect(place3, "west", "east");

		MapGraph graph = new MapGraphGenerator().generate(place1);
		graph.normalise();

		SvgModel svg = testSubject.generate(graph, 10, 10);
		svg.writeTo(new FileOutputStream("testThreePlacesConnectedSouthAndWest.svg"));

	}

	@Test
	public void testTwoPlacesConnectedNorthSouth() throws Exception {
		Place place1 = new Place(null, "place1", null);
		Place place2 = new Place(null, "place2", null);

		place1.connect(place2, "north", "south");

		MapGraph graph = new MapGraphGenerator().generate(place1);

		SvgModel svg = testSubject.generate(graph, 10, 10);

		svg.writeTo(new FileOutputStream("testTwoPlacesConnectedNorthSouth.svg"));

	}

	@Test
	public void testSinglePlace() {

		MudPlace place = new Place(null);

		MapGraph graph = new MapGraph();
		graph.add(new MapNode(place, 0, 0));

		SvgModel svg = testSubject.generate(graph, 10, 10);

		assertEquals(1, svg.size());
		assertEquals("rect", svg.get(0).name);

		assertEquals("mud-place", svg.get(0).getAttribute("class"));
		assertEquals("0", svg.get(0).getAttribute("x"));
		assertEquals("0", svg.get(0).getAttribute("y"));

		assertEquals("50", svg.get(0).getAttribute("width"));
		assertEquals("50", svg.get(0).getAttribute("height"));

	}

}
