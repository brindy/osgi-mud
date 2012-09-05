package com.brindysoft.necronomud.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.necronomud.Place;
import com.brindysoft.necronomud.places.PottersPondCreek;

public class MapGraphGeneratorTest {

	private MapGraphGenerator testSubject;

	@Before
	public void setUp() {
		testSubject = new MapGraphGenerator();
	}

	@Test
	public void testPottersPondCreek() {
		PottersPondCreek creek = new PottersPondCreek();
		creek.start();
		MapGraph actual = testSubject.generate(creek.getPlaces()[0]);
		assertNotNull(actual);
	}

	@Test
	public void normaliseThreePlacesConnectedWestWestEast() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		Place place3 = new Place(null);

		place1.connect(place2, "west", "east");
		place2.connect(place3, "west", "east");

		MapGraph actual = testSubject.generate(place1);
		actual.normalise();

		assertEquals(3, actual.size());

		assertEquals(2, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);

		assertEquals(1, actual.get(1).x);
		assertEquals(0, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

		assertEquals(0, actual.get(2).x);
		assertEquals(0, actual.get(2).y);
		assertSame(place3, actual.get(2).place);
	}

	@Test
	public void normaliseTwoPlacesConnectedWestEast() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		place1.connect(place2, "west", "east");

		MapGraph actual = testSubject.generate(place1);
		actual.normalise();

		assertEquals(2, actual.size());
		assertEquals(1, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);
		assertEquals(0, actual.get(1).x);
		assertEquals(0, actual.get(1).y);
		assertSame(place2, actual.get(1).place);
	}

	@Test
	public void normaliseTwoPlacesConnectedSouthNorth() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		place1.connect(place2, "south", "north");

		MapGraph actual = testSubject.generate(place1);
		actual.normalise();

		assertEquals(2, actual.size());
		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);
		assertEquals(0, actual.get(1).x);
		assertEquals(1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);
	}

	@Test
	public void twoPlacesConnectedSouthNort() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		place1.connect(place2, "south", "north");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(2, actual.size());
		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);
		assertEquals(0, actual.get(1).x);
		assertEquals(1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);
	}

	@Test
	public void testSouthNorthOverlap() {

		Place place1 = new Place(null, "place1", null);
		Place place2 = new Place(null, "place2", null);
		Place place3 = new Place(null, "place3", null);
		Place place4 = new Place(null, "place4", null);
		Place place5 = new Place(null, "place5", null);

		place1.connect(place2, "east", "west");
		place2.connect(place3, "north", "south");
		place3.connect(place4, "west", "east");
		place4.connect(place5, "south", "north");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(5, actual.size());

		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);

		assertEquals(1, actual.get(1).x);
		assertEquals(0, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

		assertEquals(1, actual.get(2).x);
		assertEquals(-2, actual.get(2).y);
		assertSame(place3, actual.get(2).place);

		assertEquals(0, actual.get(3).x);
		assertEquals(-2, actual.get(3).y);
		assertSame(place4, actual.get(3).place);

		assertEquals(0, actual.get(4).x);
		assertEquals(-1, actual.get(4).y);
		assertSame(place5, actual.get(4).place);
	}

	@Test
	public void testWestEastOverlap() {

		Place place1 = new Place(null, "place1", null);
		Place place2 = new Place(null, "place2", null);
		Place place3 = new Place(null, "place3", null);
		Place place4 = new Place(null, "place4", null);
		Place place5 = new Place(null, "place5", null);

		place1.connect(place2, "north", "south");
		place2.connect(place3, "west", "east");
		place3.connect(place4, "south", "north");
		place4.connect(place5, "east", "west");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(5, actual.size());

		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);

		assertEquals(0, actual.get(1).x);
		assertEquals(-1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

		assertEquals(-2, actual.get(2).x);
		assertEquals(-1, actual.get(2).y);
		assertSame(place3, actual.get(2).place);

		assertEquals(-2, actual.get(3).x);
		assertEquals(0, actual.get(3).y);
		assertSame(place4, actual.get(3).place);

		assertEquals(-1, actual.get(4).x);
		assertEquals(0, actual.get(4).y);
		assertSame(place5, actual.get(4).place);
	}

	@Test
	public void testNorthSouthOverlap() {

		Place place1 = new Place(null, "place1", null);
		Place place2 = new Place(null, "place2", null);
		Place place3 = new Place(null, "place3", null);
		Place place4 = new Place(null, "place4", null);
		Place place5 = new Place(null, "place5", null);

		place1.connect(place2, "east", "west");
		place2.connect(place3, "south", "north");
		place3.connect(place4, "west", "east");
		place4.connect(place5, "north", "south");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(5, actual.size());

		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);

		assertEquals(1, actual.get(1).x);
		assertEquals(0, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

		assertEquals(1, actual.get(2).x);
		assertEquals(2, actual.get(2).y);
		assertSame(place3, actual.get(2).place);

		assertEquals(0, actual.get(3).x);
		assertEquals(2, actual.get(3).y);
		assertSame(place4, actual.get(3).place);

		assertEquals(0, actual.get(4).x);
		assertEquals(1, actual.get(4).y);
		assertSame(place5, actual.get(4).place);
	}

	@Test
	public void testEastWestOverlap() {

		Place place1 = new Place(null, "place1", null);
		Place place2 = new Place(null, "place2", null);
		Place place3 = new Place(null, "place3", null);
		Place place4 = new Place(null, "place4", null);
		Place place5 = new Place(null, "place5", null);

		place1.connect(place2, "north", "south");
		place2.connect(place3, "east", "west");
		place3.connect(place4, "south", "north");
		place4.connect(place5, "west", "east");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(5, actual.size());

		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);

		assertEquals(0, actual.get(1).x);
		assertEquals(-1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

		assertEquals(2, actual.get(2).x);
		assertEquals(-1, actual.get(2).y);
		assertSame(place3, actual.get(2).place);

		assertEquals(2, actual.get(3).x);
		assertEquals(0, actual.get(3).y);
		assertSame(place4, actual.get(3).place);

		assertEquals(1, actual.get(4).x);
		assertEquals(0, actual.get(4).y);
		assertSame(place5, actual.get(4).place);
	}

	@Test
	public void testLoop() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		Place place3 = new Place(null);
		Place place4 = new Place(null);

		place1.connect(place2, "north", "south");
		place2.connect(place3, "east", "west");
		place3.connect(place4, "south", "north");
		place4.connect(place1, "west", "east");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(4, actual.size());

		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);

		assertEquals(0, actual.get(1).x);
		assertEquals(-1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

		assertEquals(1, actual.get(2).x);
		assertEquals(-1, actual.get(2).y);
		assertSame(place3, actual.get(2).place);

		assertEquals(1, actual.get(3).x);
		assertEquals(0, actual.get(3).y);
		assertSame(place4, actual.get(3).place);
	}

	@Test
	public void threePlacesConnectedWestNorthSouth() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		Place place3 = new Place(null);

		place1.connect(place2, "north", "south");
		place2.connect(place3, "west", "east");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(3, actual.size());

		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);

		assertEquals(0, actual.get(1).x);
		assertEquals(-1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

		assertEquals(-1, actual.get(2).x);
		assertEquals(-1, actual.get(2).y);
		assertSame(place3, actual.get(2).place);

	}

	@Test
	public void threePlacesConnectedEastNorthSouth() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		Place place3 = new Place(null);

		place1.connect(place2, "north", "south");
		place2.connect(place3, "east", "west");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(3, actual.size());

		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);

		assertEquals(0, actual.get(1).x);
		assertEquals(-1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

		assertEquals(1, actual.get(2).x);
		assertEquals(-1, actual.get(2).y);
		assertSame(place3, actual.get(2).place);

	}

	@Test
	public void threePlacesConnectedNorthNorthSouth() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		Place place3 = new Place(null);

		place1.connect(place2, "north", "south");
		place2.connect(place3, "north", "south");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(3, actual.size());

		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);

		assertEquals(0, actual.get(1).x);
		assertEquals(-1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

		assertEquals(0, actual.get(2).x);
		assertEquals(-2, actual.get(2).y);
		assertSame(place3, actual.get(2).place);

	}

	@Test
	public void twoPlacesConnectedWestEast() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		place1.connect(place2, "west", "east");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(2, actual.size());
		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);
		assertEquals(-1, actual.get(1).x);
		assertEquals(0, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

	}

	@Test
	public void twoPlacesConnectedSouthNorth() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		place1.connect(place2, "south", "north");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(2, actual.size());
		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);
		assertEquals(0, actual.get(1).x);
		assertEquals(1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

	}

	@Test
	public void twoPlacesConnectedEastWest() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		place1.connect(place2, "east", "west");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(2, actual.size());
		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);
		assertEquals(1, actual.get(1).x);
		assertEquals(0, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

	}

	@Test
	public void twoPlacesConnectedNorthSouth() {

		Place place1 = new Place(null);
		Place place2 = new Place(null);
		place1.connect(place2, "north", "south");

		MapGraph actual = testSubject.generate(place1);
		assertEquals(2, actual.size());
		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place1, actual.get(0).place);
		assertEquals(0, actual.get(1).x);
		assertEquals(-1, actual.get(1).y);
		assertSame(place2, actual.get(1).place);

	}

	@Test
	public void singlePlace() {

		MudPlace place = new Place(null);

		MapGraph actual = testSubject.generate(place);
		assertEquals(1, actual.size());
		assertEquals(0, actual.get(0).x);
		assertEquals(0, actual.get(0).y);
		assertSame(place, actual.get(0).place);

	}

}
