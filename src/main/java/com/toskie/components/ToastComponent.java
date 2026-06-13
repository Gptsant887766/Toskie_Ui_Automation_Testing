package com.toskie.components;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class ToastComponent {
    private final UtilLayer<?> util;
    private final Locator toastMsg, toastSuccess, toastError;

    public ToastComponent(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        toastMsg    = page.locator("[class*='toast'], [class*='snackbar'], [class*='notification']").first();
        toastSuccess= page.locator("[class*='toast-success'], [class*='success'][class*='toast']").first();
        toastError  = page.locator("[class*='toast-error'], [class*='error'][class*='toast']").first();
    }

    public void waitForToast()       { toastMsg.waitFor(); }
    public String getToastMessage()  { try { return toastMsg.textContent().trim(); } catch (Exception e) { return ""; } }
    public boolean isSuccessToast()  { try { return toastSuccess.isVisible(); } catch (Exception e) { return false; } }
    public boolean isErrorToast()    { try { return toastError.isVisible(); } catch (Exception e) { return false; } }
}
