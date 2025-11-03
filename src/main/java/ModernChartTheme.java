import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import java.awt.*;

public class ModernChartTheme extends StandardChartTheme {
    private final BasicStroke lineStroke;
    private final Color seriesColor;

    public ModernChartTheme() {
        super("Modern");

        // Initialize custom properties
        this.lineStroke = new BasicStroke(3.0f);
        this.seriesColor = new Color(0x6c39c1);

        // Set modern colors
        setPlotBackgroundPaint(new Color(0xfcf8ff));
        setChartBackgroundPaint(new Color(0xfcf8ff));
        setAxisLabelPaint(new Color(0x251779));
        setItemLabelPaint(new Color(0x251779));

        // Use a modern font
        setExtraLargeFont(new Font("Segoe UI", Font.BOLD, 20));
        setLargeFont(new Font("Segoe UI", Font.BOLD, 16));
        setRegularFont(new Font("Segoe UI", Font.PLAIN, 12));
        setSmallFont(new Font("Segoe UI", Font.PLAIN, 10));

        // Set gridlines
        setDomainGridlinePaint(new Color(0xE0E0E0));
        setRangeGridlinePaint(new Color(0xE0E0E0));
    }

    @Override
    public void apply(JFreeChart chart) {
        super.apply(chart);

        if (chart.getPlot() instanceof XYPlot) {
            XYPlot plot = (XYPlot) chart.getPlot();
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

            renderer.setSeriesStroke(0, lineStroke);
            renderer.setSeriesPaint(0, seriesColor);
        }
    }
}
