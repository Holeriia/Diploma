
package com.company.diploma.view.interest;

import com.company.diploma.entity.Interest;

import com.company.diploma.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "interests", layout = MainView.class)
@ViewController(id = "Interest.list")
@ViewDescriptor(path = "interest-list-view.xml")
@LookupComponent("interestsDataGrid")
@DialogMode(width = "64em")
public class InterestListView extends StandardListView<Interest> {
}