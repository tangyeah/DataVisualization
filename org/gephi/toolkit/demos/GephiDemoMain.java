package org.gephi.toolkit.demos;


import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import org.gephi.filters.api.FilterController;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder.DegreeRangeFilter;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantColor;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.toolkit.demos.plugins.preview.PreviewSketch;
import org.openide.util.Lookup;

public class GephiDemoMain {

    public static void main(String[] args) {
        GephiDemoMain met = new GephiDemoMain();
        met.script();
    }

    public void script() {
        //初始化一个project，并获取workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //获取graphModel和需要用到的controller
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);

        //输入文件，暂时仅支持csv文件，并将输入的文件和之前获取的workspace关联
        Container container;
        try {
            File file = new File("C:\\graph_data\\1.csv");
            container = importController.importFile(file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        importController.process(container, new DefaultProcessor(), workspace);
        
        //根据输入文件生成有向图，并输出该图的点数和边数
        DirectedGraph graph = graphModel.getDirectedGraph();
//        UndirectedGraph graph = graphModel.getUndirectedGraph();
        System.out.println("Nodes: " + graph.getNodeCount());
        System.out.println("Edges: " + graph.getEdgeCount());
             
/*************************************************************************************************************/
        
        //Fruchterman布局设置，可将联通的子图分离开来
        FruchtermanReingold myLayout = new FruchtermanReingold(null);
        myLayout.setGraphModel(graphModel);
        myLayout.setArea(10000.0f);
        myLayout.setGravity(10.0);
        myLayout.setSpeed(500.0);
      
        myLayout.initAlgo();
        for (int i = 0; i < 20000 && myLayout.canAlgo(); i++) {
          myLayout.goAlgo();
        }
        myLayout.endAlgo();

//        //Filter设置，根据点的度来进行过滤
//        DegreeRangeFilter degreeFilter = new DegreeRangeFilter();
////        InDegreeRangeFilter idf = new InDegreeRangeFilter();
////        OutDegreeRangeFilter odf = new OutDegreeRangeFilter();
//        degreeFilter.init(graph);
//        degreeFilter.setRange(new Range(10, Integer.MAX_VALUE));     //滤出度大于等于10的点
//        Query query = filterController.createQuery(degreeFilter);
//        GraphView view = filterController.filter(query);
//        graphModel.setVisibleView(view);
        

        
        //graph的点属性，边属性
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        
        previewModel.getProperties().putValue(PreviewProperty.ARROW_SIZE, 5);
        previewModel.getProperties().putValue(PreviewProperty.DIRECTED, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.MARGIN, 10f); //图距离frame边的距离
        previewModel.getProperties().putValue(PreviewProperty.MOVING, Boolean.FALSE);//移动画布？true时边和顶点id都没了
        previewModel.getProperties().putValue(PreviewProperty.VISIBILITY_RATIO, 1f);//显示的比例，1f为100%，点超级多的话可以显示一部分
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
        
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);//显式点的id与否
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.LIGHT_GRAY));//点标签颜色
//        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, Color.WHITE);
//        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT,);//字体
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_MAX_CHAR, 10);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_SHORTEN, Boolean.FALSE);        
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_SHOW_BOX, Boolean.FALSE);//false，不好看
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_BOX_COLOR, new DependantColor(Color.WHITE));
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_BOX_OPACITY,10f);
        previewModel.getProperties().putValue(PreviewProperty.NODE_OPACITY, 100f);//点的透明度
        previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_COLOR, new DependantColor(Color.WHITE));
        previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 2f);

        previewModel.getProperties().putValue(PreviewProperty.SHOW_EDGE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.SHOW_EDGES, Boolean.TRUE);//显不显示边
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);//边是否弯曲，弯的无向，直的有向
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);//边的透明度
        previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 0f);//箭头到点的间隔
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.BLACK));
//        previewModel.getProperties().putValue(PreviewProperty.EDGE_RESCALE_WEIGHT, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 1f);
        
        
        //显示JFrame
        G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);
        final PreviewSketch previewSketch = new PreviewSketch(target);
        previewController.refreshPreview();

        JFrame frame = new JFrame("DisplayWindow");
        frame.setLayout(new BorderLayout());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(previewSketch, BorderLayout.CENTER);

        frame.setSize(1024, 768);
        frame.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentShown(ComponentEvent e){
                previewSketch.resetZoom();
            }
        });
        frame.setVisible(true);
    }
}
