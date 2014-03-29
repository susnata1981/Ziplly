package com.ziplly.app.client.widget;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.NeighborhoodType;
import com.ziplly.app.model.overlay.AddressComponent;

public class GooglePlacesWidget implements GooglePlacesDataExporter {
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

	private TreeMap<NeighborhoodType, String> addressInfoMap;
	private SignupActivityPresenter presenter;

	private HelpInline helpInline;
	private TextBox field;
	private ControlGroup cg;
	private Listener listener;
	private AddYourNeighborhoodWidget widget;
	private Anchor clearAddressAnchor;
	private NeighborhoodDTO selectedNeighborhood;
	private NeighborhoodBuilder neigborhoodBuilder;
	
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
		this.neigborhoodBuilder = new NeighborhoodBuilder(this);
		StyleHelper.show(clearAddressAnchor.getElement(), false);
		StyleHelper.show(helpInline.getElement(), false);
		initializePlacesApi(field.getElement());
		setupHandler();
		addressInfoMap =
		    new TreeMap<NeighborhoodType, String>(new Comparator<NeighborhoodType>() {

			    @Override
			    public int compare(NeighborhoodType o1, NeighborhoodType o2) {
				    return (o1.getPriority() - o2.getPriority());
			    }
		    });
	}

	private void setupHandler() {
		field.addChangeHandler(new ChangeHandler() {

			@Override
      public void onChange(ChangeEvent event) {
				addressInfoMap.clear();
				selectedNeighborhood = null;
				internalValidateAddress();
			}
		});
		
		clearAddressAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				clear();
				reset();
			}
		});
	}

	/**
	 * GOOGLE PLACES API INITIALIZATION
	 */
	public native void initializePlacesApi(Element elem) /*-{
		var options = {
			types : [ "geocode" ],
			regions : [ 'neighborhood', 'sublocality', 'locality', 
					'administrative_area3', 'administrative_area2',
					'administrative_area1', 'country', 'postal_code' ]
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

		JsArray<AddressComponent> components = getAddressComponents();
		for (int i = 0; i < components.length(); i++) {
			AddressComponent ac = components.get(i);
			System.out.println(ac.getType() + "-->" + ac.getLongName());
			NeighborhoodType ntype = NeighborhoodType.getNeighborhoodType(ac.getType());
			if (ntype != null) {
				addressInfoMap.put(ntype, ac.getLongName());
			}
		}

		internalValidateAddress();
	}

	public boolean validateAddress() {
		if (selectedNeighborhood == null) {
			internalValidateAddress();
			return false;
		}
		return true;
	}
	
	private boolean internalValidateAddress() {
		if ((!addressInfoMap.containsKey(NeighborhoodType.LOCALITY) 
				    && !addressInfoMap.containsKey(NeighborhoodType.SUB_LOCALITY))
		    || !addressInfoMap.containsKey(NeighborhoodType.ADMINISTRATIVE_AREA_LEVEL_2)
		    || !addressInfoMap.containsKey(NeighborhoodType.ADMINISTRATIVE_AREA_LEVEL_1)
		    || !addressInfoMap.containsKey(NeighborhoodType.COUNTRY)
		    || !addressInfoMap.containsKey(NeighborhoodType.POSTAL_CODE)) {

			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(StringConstants.INVALID_ADDRESS);
			StyleHelper.show(helpInline.getElement(), true);
			return false;
		}

		if (!addressInfoMap.containsKey(NeighborhoodType.NEIGHBORHOOD)) {
			cg.setType(ControlGroupType.WARNING);
			helpInline.setText(StringConstants.NEIGHBORHOOD_NOT_FOUND);
			StyleHelper.show(helpInline.getElement(), true);
			displayAddYourNeighborhoodWidget();
			return false;
		}

		StyleHelper.show(helpInline.getElement(), false);
		cg.setType(ControlGroupType.SUCCESS);
		if (listener != null) {
			listener.onChange(getNeighborhood());
		}
		return true;
	}

	public void displayAddYourNeighborhoodWidget() {
		NeighborhoodDTO neighborhood = neigborhoodBuilder.getTemplateNeighborhoodWithParent();
		widget = new AddYourNeighborhoodWidget(neighborhood, presenter);
		widget.show(true);
		widget.showLoading(true);
		presenter.getNeighborhoodList(neighborhood);
	}

	public NeighborhoodDTO getNeighborhood() {
		return neigborhoodBuilder.getNeighborhood();
	}

	/**
	 * @return Array of address components in JSON format
	 */
	public native JsArray<AddressComponent> getAddressComponents() /*-{
		return $wnd.places.address_components;
	}-*/;

	public void clear() {
		selectedNeighborhood = null;
		addressInfoMap.clear();
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
		}
		selectedNeighborhood = n;
		setState(n);
		StyleHelper.show(clearAddressAnchor.getElement(), true);
	}

	private void setState(NeighborhoodDTO n) {
		if (n != null) {
			NeighborhoodDTO temp = n;
			for(NeighborhoodType type : NeighborhoodType.getSortedValues()) {
				if (type == n.getType()) {
					addressInfoMap.put(type, temp.getName());
				}
				temp = n.getParentNeighborhood();
				if (temp == null) {
					break;
				}
			}
		}
  }

	private void reset() {
		addressInfoMap.clear();
  }

	public void displayNeighborhoodList(List<NeighborhoodDTO> neighborhoods) {
		widget.displayNeighborhoods(neighborhoods);
		widget.showLoading(false);
	}

	public void displayErrorDuringNeighborhoodSelection(String failedToAddNeighborhood,
      AlertType error) {
	  
		if (widget != null) {
			widget.displayMessage(failedToAddNeighborhood, error);
		}
  }

	@Override
  public String getDataOfType(NeighborhoodType type) {
		return addressInfoMap.get(type);
  }

	@Override
  public TreeMap<NeighborhoodType, String> getData() {
		return addressInfoMap;
  }
}
