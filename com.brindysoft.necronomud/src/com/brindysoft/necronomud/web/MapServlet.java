package com.brindysoft.necronomud.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.mpi.MudWorld;

@Component
public class MapServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private MudWorld world;

	@Reference
	public void setWorld(MudWorld world) {
		this.world = world;
	}

	@Reference(multiple = true, dynamic = true, optional = true)
	public void addHttpService(HttpService http) throws ServletException, NamespaceException {
		http.registerServlet("/mud/necro/map", this, null, null);
	}

	public void removeHttpService(HttpService http) {
		http.unregister("/mud/necro/map");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		MapGraph graph = new MapGraphGenerator().generate(world.findPlaceByTag("0001"));
		graph.normalise();
		SvgModel svg = new GraphSvgGenerator().generate(graph, 100, 100);

		resp.setContentType("image/svg+xml");
		svg.writeTo(resp.getOutputStream());

	}

}
