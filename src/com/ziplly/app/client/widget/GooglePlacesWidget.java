package com.ziplly.app.client.widget;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.NeighborhoodType;
import com.ziplly.app.model.PostalCodeDTO;
import com.ziplly.app.model.overlay.AddressComponent;

public class GooglePlacesWidget {

	enum AddressDetails {
		// STREET_NUMBER(0, "street_number"),
		// ROUTE(1, "route"),
		NEIGHBORHOOD(3, "neighborhood"),
		LOCALITY(4, "locality"),
		SUB_LOCALITY(5, "sublocality"),
		ADMINISTRATIVE_AREA_KEY_3(6, "administrative_area_level_3"),
		ADMINISTRATIVE_AREA_KEY_2(7, "administrative_area_level_2"),
		ADMINISTRATIVE_AREA_KEY_1(8, "administrative_area_level_1"),
		ADMINISTRATIVE_AREA_COUNTRY(9, "country"),
		POSTAL_CODE(10, "postal_code");

		private String name;
		private int priority;

		private AddressDetails(int priority, String name) {
			this.priority = priority;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public int getPriority() {
			return priority;
		}

		public static AddressDetails getAddressDetails(String name) {
			for (AddressDetails ad : values()) {
				if (ad.getName().equalsIgnoreCase(name)) {
					return ad;
				}
			}
			return null;
		}
	}

	private static final String STREET_NUMBER_KEY = "street_number";
	private static final String ROUTE_KEY = "route";
	private static final String NEIGHBORHOOD_KEY = "neighborhood";
	private static final String LOCALITY_KEY = "locality";
	private static final String SUB_LOCALITY_KEY = "sublocality";
	private static final String ADMINISTRATIVE_AREA_KEY_3 = "administrative_area_level_3";
	private static final String ADMINISTRATIVE_AREA_KEY_2 = "administrative_area_level_2";
	private static final String ADMINISTRATIVE_AREA_KEY_1 = "administrative_area_level_1";
	private static final String ADMINISTRATIVE_AREA_COUNTRY = "country";
	private static final String POSTAL_CODE_KEY = "postal_code";

	private Map<AddressDetails, String> addressInfoMap;
	private SignupActivityPresenter presenter;
	private boolean foundValidAddress;
	private String streetNumber;
	private String streetName;
	private String neighborhoodName;
	private String locality;
	private String county;
	private String postalCode;

	private HelpInline helpInline;
	private TextBox field;
	private ControlGroup cg;
	private String state;
	private String country;
	private Listener listener;
	private String subLocality;
	private Anchor clearAddressAnchor;
	private String administrative_area_3;
	private AddYourNeighborhoodWidget widget;

	public GooglePlacesWidget(TextBox field,
	    ControlGroup cg,
	    HelpInline helpInline,
	    Anchor clearAddressAnchor,
	    Listener listener) {
		this.field = field;
		this.cg = cg;
		this.helpInline = helpInline;
		this.clearAddressAnchor = clearAddressAnchor;
		this.listener = listener;
		StyleHelper.show(clearAddressAnchor.getElement(), false);
		StyleHelper.show(helpInline.getElement(), false);
		initializePlacesApi(field.getElement());
		setupHandler();
		addressInfoMap =
		    new TreeMap<GooglePlacesWidget.AddressDetails, String>(new Comparator<AddressDetails>() {

			    @Override
			    public int compare(AddressDetails o1, AddressDetails o2) {
				    return (o1.getPriority() - o2.getPriority());
			    }

		    });
	}

	private void setupHandler() {
		field.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if (!foundValidAddress) {
//					clear();
//					addressInfoMap.clear();
//					streetNumber =
//					    streetName =
//					        neighborhoodName = administrative_area_3 = county = state = postalCode = null;
					validateAddress();
				}
			}
		});

		field.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				foundValidAddress = false;
				addressInfoMap.clear();
			}
		});

		clearAddressAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				clear();
			}
		});
	}

	/**
	 * GOOGLE PLACES API INITIALIZATION
	 */
	public native void initializePlacesApi(Element elem) /*-{
		var options = {
			types : [ "geocode" ],
			regions : [ 'neighborhood', 'locality', 'postal_code', 'country',
					'administrative_area1', 'administrative_area2',
					'administrative_area3' ]
		};

		var autocomplete = new $wnd.google.maps.places.Autocomplete(elem,
				options);

		var that = this;
		$wnd.google.maps.event
				.addListener(
						autocomplete,
						'place_changed',
						function() {
							var places = autocomplete.getPlace();
							console.log(places);
							$wnd.places = autocomplete.getPlace();
							that.@com.ziplly.app.client.widget.GooglePlacesWidget::populateAddressFields()();
						});
	}-*/;

	/**
	 * Callback handler to populate Address fields
	 */
	public void populateAddressFields() {
		addressInfoMap.clear();
		streetNumber =
		    streetName = neighborhoodName = administrative_area_3 = county = state = postalCode = null;

		JsArray<AddressComponent> components = getAddressComponents();
		for (int i = 0; i < components.length(); i++) {
			AddressComponent ac = components.get(i);
			System.out.println(ac.getType() + "-->" + ac.getShortName());
			AddressDetails addressDetails = AddressDetails.getAddressDetails(ac.getType());
			if (addressDetails != null) {
				addressInfoMap.put(addressDetails, ac.getLongName());
			}
		}

		validateAddress();
	}

	public boolean validateAddress() {
		if ((!addressInfoMap.containsKey(AddressDetails.LOCALITY) 
				    && !addressInfoMap.containsKey(AddressDetails.SUB_LOCALITY))
		    || !addressInfoMap.containsKey(AddressDetails.ADMINISTRATIVE_AREA_KEY_2)
		    || !addressInfoMap.containsKey(AddressDetails.ADMINISTRATIVE_AREA_KEY_1)) {

			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(StringConstants.INVALID_ADDRESS);
			StyleHelper.show(helpInline.getElement(), true);
			return false;
		}

		if (!addressInfoMap.containsKey(AddressDetails.NEIGHBORHOOD)
		    && !addressInfoMap.containsKey(AddressDetails.ADMINISTRATIVE_AREA_KEY_3)) {

			displayAddYourNeighborhoodWidget();
			return false;
		}

		foundValidAddress = true;
		StyleHelper.show(helpInline.getElement(), false);
		StyleHelper.show(clearAddressAnchor.getElement(), true);
		cg.setType(ControlGroupType.SUCCESS);
		if (listener != null) {
			listener.onChange(getNeighborhood());
		}
		return true;
	}

	public void displayAddYourNeighborhoodWidget() {
		NeighborhoodDTO neighborhood = getNeighborhood();
		widget = new AddYourNeighborhoodWidget(neighborhood, presenter);
		widget.show(true);
		widget.showLoading(true);
		presenter.getNeighborhoodList(neighborhood);
	}

	public NeighborhoodDTO getNeighborhood() {
		assignNeighborhoods();

		Date now = new Date();
		NeighborhoodDTO neighborhood = new NeighborhoodDTO();
		neighborhood.setName(neighborhoodName);
		neighborhood.setCity(locality);
		neighborhood.setState(state);
		neighborhood.setTimeCreated(now);
		neighborhood.setType(NeighborhoodType.NEIGHBORHOOD);

		PostalCodeDTO p = new PostalCodeDTO();
		p.setPostalCode(postalCode);
		neighborhood.addPostalCode(p);

		NeighborhoodDTO parent = new NeighborhoodDTO();
		parent.setName(locality);
		parent.setState(state);
		parent.setTimeCreated(now);
		parent.setType(NeighborhoodType.LOCALITY);

		NeighborhoodDTO grandParent = new NeighborhoodDTO();
		grandParent.setName(county);
		grandParent.setState(state);
		grandParent.setType(NeighborhoodType.ADMINISTRATIVE_AREA_LEVEL_2);
		grandParent.setTimeCreated(now);

		NeighborhoodDTO greatGrandParent = new NeighborhoodDTO();
		greatGrandParent.setName(state);
		greatGrandParent.setState(state);
		greatGrandParent.setType(NeighborhoodType.ADMINISTRATIVE_AREA_LEVEL_1);
		greatGrandParent.setTimeCreated(now);

		NeighborhoodDTO greatGreatGrandParent = new NeighborhoodDTO();
		greatGreatGrandParent.setName(country);
		greatGreatGrandParent.setState(country);
		greatGreatGrandParent.setType(NeighborhoodType.COUNTRY);
		greatGreatGrandParent.setTimeCreated(now);

		greatGrandParent.setParentNeighborhood(greatGreatGrandParent);
		grandParent.setParentNeighborhood(greatGrandParent);
		parent.setParentNeighborhood(grandParent);
		neighborhood.setParentNeighborhood(parent);

		return neighborhood;
	}

	private void assignNeighborhoods() {
		neighborhoodName = addressInfoMap.get(AddressDetails.NEIGHBORHOOD);
		locality = getLocality();

		if (addressInfoMap.containsKey(AddressDetails.ADMINISTRATIVE_AREA_KEY_3)
		    && !addressInfoMap.containsKey(AddressDetails.NEIGHBORHOOD)) {
			neighborhoodName = addressInfoMap.get(AddressDetails.LOCALITY);
			locality = addressInfoMap.get(AddressDetails.ADMINISTRATIVE_AREA_KEY_3);
		}

		county = addressInfoMap.get(AddressDetails.ADMINISTRATIVE_AREA_KEY_2);
		state = addressInfoMap.get(AddressDetails.ADMINISTRATIVE_AREA_KEY_1);
		country = addressInfoMap.get(AddressDetails.ADMINISTRATIVE_AREA_COUNTRY);
		postalCode = addressInfoMap.get(AddressDetails.POSTAL_CODE);
	}

	private String getLocality() {
		if (addressInfoMap.containsKey(AddressDetails.LOCALITY)) {
			return addressInfoMap.get(AddressDetails.LOCALITY);
		} else {
			return addressInfoMap.get(AddressDetails.SUB_LOCALITY);
		}
  }

	/**
	 * @return Array of address components in JSON format
	 */
	public native JsArray<AddressComponent> getAddressComponents() /*-{
		return $wnd.places.address_components;
	}-*/;

	public void clear() {
		streetName = null;
		locality = null;
		neighborhoodName = null;
		county = null;
		postalCode = null;
		field.setText("");
		StyleHelper.show(clearAddressAnchor.getElement(), false);
		clearError();
	}

	private void clearError() {
		cg.setType(ControlGroupType.NONE);
		StyleHelper.show(helpInline.getElement(), false);
	}

	public static interface Listener {
		public void onChange(NeighborhoodDTO n);
	}

	public void setStatus(String status, ControlGroupType type) {
		helpInline.setText(status);
		StyleHelper.show(helpInline.getElement(), true);
		cg.setType(type);
	}

	public void setPresenter(SignupActivityPresenter presenter) {
		this.presenter = presenter;
	}

	public void addedNewNeighborhood(NeighborhoodDTO n) {
		if (widget != null) {
			widget.show(false);
			foundValidAddress = true;
			addressInfoMap.put(AddressDetails.NEIGHBORHOOD, n.getName());
			addressInfoMap.put(AddressDetails.LOCALITY, n.getParentNeighborhood().getName());
			addressInfoMap.put(AddressDetails.ADMINISTRATIVE_AREA_KEY_1, n.getState());
			addressInfoMap.put(AddressDetails.ADMINISTRATIVE_AREA_KEY_2, n
			    .getParentNeighborhood()
			    .getParentNeighborhood()
			    .getName());
			addressInfoMap.put(AddressDetails.POSTAL_CODE, n.getPostalCodes().get(0).getPostalCode());
			addressInfoMap.put(AddressDetails.ADMINISTRATIVE_AREA_COUNTRY, n
			    .getParentNeighborhood()
			    .getParentNeighborhood()
			    .getParentNeighborhood()
			    .getName());
			StyleHelper.show(clearAddressAnchor.getElement(), true);
		}
	}

	public void displayNeighborhoodList(List<NeighborhoodDTO> neighborhoods) {
		widget.displayNeighborhoods(neighborhoods);
		widget.showLoading(false);
	}
}
