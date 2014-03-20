package fr.dush.mediamanager.plugins.jmplayer;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Thomas Duchatelle
 */
class ArgReader {

    private String expectedParamName;

    //    @Getter
    private String value;

    public ArgReader(String expectedParamName) {
        this.expectedParamName = expectedParamName;
    }

    public synchronized void doIt(String paramName, String value) {
        if (StringUtils.equals(paramName, expectedParamName)) {
            this.value = value;
            notifyAll();
        }
    }

    public String getValue() {
        return value;
    }
}
