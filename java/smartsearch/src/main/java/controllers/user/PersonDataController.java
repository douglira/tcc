package controllers.user;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import dao.AddressDAO;
import dao.PersonDAO;
import models.Address;
import models.Person;
import models.User;

@WebServlet("/account/me/data")
public class PersonDataController extends HttpServlet {
	private static final long serialVersionUID = 6580597678283605205L;

	public PersonDataController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		Gson gJson = new Gson();
		
		HttpSession session = ((HttpServletRequest) request).getSession();
		User user = (User) session.getAttribute("loggedUser");
		
		Person person = new Person(user);
		person = new PersonDAO(true).findByUser(person);
		
		Address address = new AddressDAO(true).findByPerson(person.getId());
		
		if (address != null) {
			person.setAddress(address);
		}
		
		out.print(gJson.toJson(person));
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		int personId = Integer.parseInt(request.getParameter("personId"));
		String accountOwner = request.getParameter("accountOwner");
		long tel = Long.parseLong(request.getParameter("tel"));
		long cnpj = Long.parseLong(request.getParameter("cnpj"));
		String corporateName = request.getParameter("corporateName");
		long stateRegistration = Long.parseLong(request.getParameter("stateRegistration"));

		int addressId = 0;
		if (request.getParameter("addressId") != null) {
			addressId = Integer.parseInt(request.getParameter("addressId"));
		}
		String street = request.getParameter("street");
		String additionalData = request.getParameter("additionalData");
		String district = request.getParameter("district");
		int buildingNumber = Integer.parseInt(request.getParameter("buildingNumber"));
		String city= request.getParameter("city");
		String provinceCode = request.getParameter("provinceCode");
		String postalCode = request.getParameter("postalCode");
		
		
		Person person = new Person();
		person.setId(personId);
		person.setAccountOwner(accountOwner);
		person.setTel(tel);
		person.setCnpj(cnpj);
		person.setCorporateName(corporateName);
		person.setStateRegistration(stateRegistration);
		
		Address address = new Address();
		address.setId(addressId);
		address.setStreet(street);
		address.setAdditionalData(additionalData);
		address.setDistrict(district);
		address.setBuildingNumber(buildingNumber);
		address.setCity(city);
		address.setProvinceCode(provinceCode);
		address.setPostalCode(postalCode);
		address.setPerson(person);
		
		new PersonDAO(true).update(person);
		
		if (addressId == 0) {
			new AddressDAO(true).create(address);
		} else {			
			new AddressDAO(true).update(address);
		}
		
		
		out.print("{\"msg\": \"Dados alterados com sucesso!\"}");
		out.close();
	}

}
