/* script.js
 * This script contains all js code that can be called from the SVG.
 * A couple of tips:
 * All comments must be slash-star style and not slash-slash, because this whole script gets
 *    mushed into one line.
 * There is a variable called chart_num that gets set to a unique chart_num. Feel free to use it.
 * FireChartSVG.getChart(chart_num) is how you access the FireChartSVG that owns the svg in question.
 * Example: FireChartSVG.printMessage(FireChartSVG.getChart(chart_num).getChartNum());
 *    prints out the chart number.
 *
 *************************/
importPackage(Packages.javax.swing);
importPackage(Packages.org.fhaes.neofhchart);

function doIt() { /* just for demonstration purposes */
    /* You can call arbitrary functions on FireChartSVG */
    FireChartSVG.printMessage("Hello, ECMAScript.");
    FireChartSVG.printMessage(FireChartSVG.getChart(chart_num).getChartNum());
    
    /* You can even do cool stuff like create new JFrames. */
    /*var frame = new JFrame("My test frame");
    var label = new JLabel("Hello from Java objects created in ECMAScript!");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    frame.getContentPane().add(label);
    frame.setSize(400, 100);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);*/
}

function paddingGrouperOnClick(evt) {
    var id = FireChartSVG.getChart(chart_num).drawAnnoteLine(evt.clientX);
}
