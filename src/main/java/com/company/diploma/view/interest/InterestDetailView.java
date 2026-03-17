package com.company.diploma.view.interest;

import com.company.diploma.entity.Interest;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "interests/:id", layout = MainView.class)
@ViewController(id = "Interest.detail")
@ViewDescriptor(path = "interest-detail-view.xml")
@EditedEntityContainer("interestDc")
public class InterestDetailView extends StandardDetailView<Interest> {
}