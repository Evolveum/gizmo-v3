package sk.lazyman.gizmo.component.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.string.StringValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateRangePickerBehavior extends AjaxEventBehavior {

    public DateRangePickerBehavior() {
        super("change");
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        StringBuilder componentSb = new StringBuilder("$('#").append(component.getMarkupId()).append("')");
        String componentSelector = componentSb.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("if (typeof ");
        sb.append(componentSelector);
        sb.append(".daterangepicker === \"function\") {\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        sb.append(componentSelector);
        sb.append(".daterangepicker({ " +
//                "\"startDate\": \""+ component.getDefaultModelObject().format(formatter) + "\", "+
//                "\"endDate\": \"" + endDate.format(formatter) + "\"," +
                "\"singleDatePicker\": true, " +
                "\"locale\": { " +
                    "\"format\":\"DD/MM/YYYY\", " +
                    "\"firstDay\":1" +
                    "}" +
                "});");
        sb.append("}");
        sb.append(getCallbackScript());
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }

    @Override
    protected void onEvent(AjaxRequestTarget target) {
        System.out.println("asdasda");
    }

//    @Override
//    protected void respond(AjaxRequestTarget target) {
//        RequestCycle cycle = RequestCycle.get();
//        WebRequest webRequest = (WebRequest) cycle.getRequest();
//        StringValue param1 = webRequest.getQueryParameters().getParameterValue("start");
//        StringValue param2 = webRequest.getQueryParameters().getParameterValue("end");
//    }


}
