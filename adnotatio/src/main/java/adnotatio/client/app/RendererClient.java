package adnotatio.client.app;

import java.util.List;

import adnotatio.common.data.IPropertiesContainer;
import adnotatio.common.io.ResourceLoader;
import adnotatio.common.io.ResourceLoaderBarrier;
import adnotatio.common.io.XMLLoader;
import adnotatio.common.xml.IXmlElement;
import adnotatio.common.xml.gwt.GwtXmlElement;
import adnotatio.renderer.templates.FieldI18N;
import adnotatio.renderer.templates.FieldInfo;
import adnotatio.renderer.templates.FieldInfoFactoryRegistry;
import adnotatio.renderer.templates.FieldPanel;
import adnotatio.renderer.templates.IFieldI18N;
import adnotatio.renderer.templates.IFieldInfoFactory;
import adnotatio.renderer.templates.TemplateBuilder;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class RendererClient implements EntryPoint {

    abstract static class Callback implements AsyncCallback {
        public void onFailure(Throwable caught) {
            Window.alert("ERROR: " + caught.getMessage());
        }
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final Button button = new Button("Click me");
        final FlowPanel internalPanel = new FlowPanel();

        final FieldInfoFactoryRegistry registry = new FieldInfoFactoryRegistry();
        registry.addFieldInfoFactory("titi", null, new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new FieldInfo(panel, e) {

                    private String fValue;

                    public void clear() {
                        setValue(getHTML(), null);
                    }

                    private HTML getHTML() {
                        return (HTML) getWidget();
                    }

                    public Object getValue() {
                        return fValue;
                    }

                    protected Widget newWidget() {
                        HTML widget = new HTML();
                        setValue(widget, null);
                        return widget;
                    }

                    /**
                     * @param widget
                     * @param value
                     */
                    private void setValue(HTML widget, Object value) {
                        fValue = value != null
                            ? value.toString()
                            : getLabelFromAttributes();
                        widget.setHTML("<h1>" + fValue + "</h1>");
                    }

                    protected boolean setValue(Object value) {
                        HTML widget = getHTML();
                        setValue(widget, value);
                        return true;
                    }

                };
            }
        });

        button.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                reloadTemplates(registry, new Callback() {
                    public void onSuccess(Object result) {
                        TemplateBuilder builder = (TemplateBuilder) result;
                        final FieldPanel w = builder.buildPanel("test");
                        w.addSubmitListener(new ClickListener() {
                            public void onClick(Widget sender) {
                                w.setEnabled(false);
                                IPropertiesContainer values = FieldPanelUtil
                                    .getFieldValues(w);
                                String str = values.toString();
                                Window.alert(str);
                                w.setEnabled(true);
                            }
                        });
                        w.addCancelListener(new ClickListener() {
                            public void onClick(Widget sender) {
                                w.setEnabled(false);
                                Window.alert("Cancelled...");
                                w.setEnabled(true);
                            }
                        });
                        internalPanel.clear();
                        internalPanel.add(w);
                    }
                });
            }
        });
        FlowPanel panel = new FlowPanel();
        panel.add(button);
        panel.add(internalPanel);
        RootPanel.get().add(panel);
    }

    /**
     * @param registry
     * @param callback
     */
    private void reloadTemplates(
        FieldInfoFactoryRegistry registry,
        final Callback callback) {
        final TemplateBuilder builder = new TemplateBuilder(registry);
        final ResourceLoaderBarrier barrier = new ResourceLoaderBarrier() {
            protected void onFinish(List successList, List failureList) {
                callback.onSuccess(builder);
            }
        };
        barrier.add(
            new ResourceLoader("RendererClient-template.i18n"),
            new Callback() {
                public void onSuccess(Object result) {
                    Response response = (Response) result;
                    String text = response.getText();
                    IFieldI18N constants = new FieldI18N(text);
                    builder.setConstants(constants);
                }
            });
        barrier.add(
            new XMLLoader("RendererClient-template.xml"),
            new Callback() {
                public void onSuccess(Object o) {
                    IXmlElement e = new GwtXmlElement(
                        (com.google.gwt.xml.client.Element) o);
                    builder.addTemplate("test", e);
                }
            });
        barrier.load();
    }

}
