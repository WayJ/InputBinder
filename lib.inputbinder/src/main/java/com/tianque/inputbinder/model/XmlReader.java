package com.tianque.inputbinder.model;

import android.content.res.Resources;
import android.text.TextUtils;
import com.tianque.inputbinder.item.InputItemType;
import com.tianque.inputbinder.util.ContextUtils;
import com.tianque.inputbinder.util.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlReader implements InputReaderInf{

    private final String TAG="inputbinder.XMLReader";
    private static final String resFileName_id = "id";
    private static String mParamPrefix;
    private XmlParser mConfiguration;
    private String nodeName;
    private String mResourcePre;
    private static boolean mFirst = true;

    public XmlReader(int viewConfigXmlResId,String nodeName) {
        init(viewConfigXmlResId);
        this.nodeName = nodeName;
    }

    public void init(int viewConfigXmlResId) {
        try {
            if(viewConfigXmlResId>0)
                mConfiguration = new XmlParser(ContextUtils.getApplicationContext()
                    .getResources().openRawResource(viewConfigXmlResId));
        } catch (Resources.NotFoundException e) {
            Logging.e("ViewConfig 文件必须放在raw资源文件中");
            e.printStackTrace();
        }
    }

    public String getPrefix() {
        return mParamPrefix;
    }

    public void setPrefix(String prefix) {
        mParamPrefix = prefix;
    }

    public  String getmResourcePre() {
        return mResourcePre==null?"":mResourcePre;
    }

    public  void setmResourcePre(String mResourcePre) {
        this.mResourcePre = mResourcePre;
    }

    public List<ViewAttribute> read() {
        mFirst = true;
        List<ViewAttribute> viewAttributeList=new ArrayList<>();

        List<NodeList> allParentNodes = new ArrayList<NodeList>();
        getConfigNodes(nodeName, allParentNodes);

        for (NodeList nodes : allParentNodes) {
            int length = nodes.getLength();
            for (int i = 0; i < length; i++) {
                Node n = nodes.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                ViewAttribute config = new ViewAttribute();

                Element element = (Element) nodes.item(i);
                String key = element.getAttribute("key");
                String uploadKey = element.getAttribute("request-key");
                String requestDefault = element.getAttribute("request-default");
                String type = element.getAttribute("type");
                Boolean required = element.getAttribute("required").equals("true");

                boolean optionalCallback = element.getAttribute("optional-callback").equals("true");
                boolean noPrefix = element.getAttribute("no-prefix").equals("true");
                boolean clearSelection = element.getAttribute("clear-selection").equals("true");
                String requiredRemind = element.getAttribute("required-remind");
                String validateMethod = element.getAttribute("validate-method");
                String visible = element.getAttribute("visible");
                String dependent = element.getAttribute("dependent");
                String parm = element.getAttribute("parm");

                int maxLength = convertToInt(element.getAttribute("maxLength"), 10000);
                int minLength = convertToInt(element.getAttribute("minLength"), 0);

                NodeList cNodes = element.getChildNodes();

                config.key = key;
                config.viewId = findIdByName(key);
                config.requestKey = (noPrefix || uploadKey.trim().length() == 0) ? uploadKey
                        : mParamPrefix + uploadKey;
                config.requestDefault = requestDefault.trim().length() == 0 ? null : requestDefault;
//                config.type = TextUtils.isEmpty(type)? InputItemType.Text.getValue():type;//fixme 这里扩展type未处理
                config.type = InputItemType.get(TextUtils.isEmpty(type)?"text":type).getValue();
                config.required = required;
                config.requiredRemind = requiredRemind;
                config.validateMethoid = validateMethod;
                config.optionalCallback = optionalCallback;
                config.addClearSelection = clearSelection;
                config.visible = visible;
                config.maxLength = maxLength;
                config.minLength = minLength;
                config.dependent = dependent;
                config.parm = parm;

                if(config.viewId<=0){
                    Logging.e("找不到R.id."+config.key);
                }else
                    viewAttributeList.add(config);
            }
        }

        return viewAttributeList;

    }

    private void getConfigNodes(String rootNodeName, List<NodeList> nodes) {
        if (TextUtils.isEmpty(rootNodeName)) {
            return;
        }

        NodeList rootNodes = mConfiguration.getRootElement().getElementsByTagName(rootNodeName);
        if (rootNodes.getLength() > 1) {
            throw new RuntimeException("The config name must be unique----->:" + rootNodeName);
        }

        if (rootNodes.getLength() == 0) {
            return;
        }

        Node rootNode = rootNodes.item(0);
        if (rootNode.getNodeType() != Node.ELEMENT_NODE) {
            return;
        }
        if (mFirst) {
            mParamPrefix = mParamPrefix == null ? ((Element) rootNode).getAttribute("key-prefix")
                    : mParamPrefix;
//            mResourcePre = mResourcePre == null ? ((Element) rootNode).getAttribute("packageName")
//                    : mResourcePre;
            mFirst = false;
        }
        String parentName = ((Element) rootNode).getAttribute("parent");
        getConfigNodes(parentName, nodes);

        if (rootNode.hasChildNodes()) {
            nodes.add(rootNode.getChildNodes());
        }

        return;
    }

    private int findIdByName(String name) {
        if(mResourcePre!=null)
            return ContextUtils.getApplicationContext().getResources().getIdentifier(mResourcePre+":id/"+name,null,null);
        else
            return ContextUtils.getApplicationContext().getResources().getIdentifier(name,resFileName_id,ContextUtils.getApplicationContext().getPackageName());
    }

    private int convertToInt(String text,int defaultInt){
        if(TextUtils.isEmpty(text))
            return defaultInt;
        try{
            return Integer.valueOf(text);
        }catch (Exception e){
            return defaultInt;
        }
    }


    class XmlParser {
        private Element mRootElement;

        public XmlParser(InputStream ins){
//		System.out.println("PKG_NAME:" + GlobalConstant.PKG_NAME);
            if(ins == null){
                throw new NullPointerException("The inputstream is null");
            }

            Document mDom = null;
            try {
                mDom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ins);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            if(mDom != null){
                mRootElement = mDom.getDocumentElement();
            } else {
                throw new NullPointerException("Can not parse the xml");
            }
        }

        public Element getRootElement(){
            return mRootElement;
        }


    }

}
