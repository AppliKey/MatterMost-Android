/**
 * @author dawson dong
 */

package com.applikey.mattermost.utils.kissUtils.shell;

public class ShellResult {

    public static final String TAG = ShellResult.class.getSimpleName();

    private int resultCode;
    private String successMsg;
    private String errorMsg;

    public ShellResult() {
        this.resultCode = -1;
    }

    public ShellResult(int resultCode) {
        this.resultCode = resultCode;
    }

    public ShellResult(int resultCode, String successMsg, String errorMsg) {
        this.resultCode = resultCode;
        this.successMsg = successMsg;
        this.errorMsg = errorMsg;
    }

    public boolean success() {
        return (resultCode == 0);
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
