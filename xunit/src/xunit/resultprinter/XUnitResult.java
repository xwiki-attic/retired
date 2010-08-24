/*
 * Created on 2005-sep-09
 * 
 */
package xunit.resultprinter;

/**
 * @author eabduha
 *
 */
public class XUnitResult implements ResultPrinterInterface {

    private String FormattedResult;

    public XUnitResult() {
        FormattedResult = "";
    }
    
    public XUnitResult(String Result) {
        FormattedResult = Result;
    }

    public void ConcatResult (String Result){
        FormattedResult = FormattedResult.concat("\n"+Result);
    }
    
    /* (non-Javadoc)
     * @see xunit.resultprinter.ResultPrinterInterface#getResult()
     */
    public String getResult() {
        return FormattedResult;
    }

}
