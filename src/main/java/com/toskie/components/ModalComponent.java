package com.toskie.components;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class ModalComponent {
    private final UtilLayer<?> util;
    private final Locator modalContainer, modalTitle, confirmBtn, cancelBtn, closeBtn, modalBody;

    public ModalComponent(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        modalContainer = page.locator("[class*='modal'], [role='dialog']").first();
        modalTitle     = page.locator("[class*='modal-title'], [class*='modal'] h2, [class*='modal'] h3").first();
        confirmBtn     = page.locator("//button[normalize-space()='Confirm'] | //button[normalize-space()='Yes'] | //button[normalize-space()='OK']").first();
        cancelBtn      = page.locator("//button[normalize-space()='Cancel'] | //button[normalize-space()='No']").first();
        closeBtn       = page.locator("[class*='modal'] [class*='close'], [aria-label='close'], button[class*='close']").first();
        modalBody      = page.locator("[class*='modal-body'], [class*='modal-content'] p").first();
    }

    public boolean isModalVisible() { try { return modalContainer.isVisible(); } catch (Exception e) { return false; } }
    public String getModalTitle()   { try { return modalTitle.textContent().trim(); } catch (Exception e) { return ""; } }
    public void clickConfirm()      { util.click(confirmBtn, "Modal Confirm"); }
    public void clickCancel()       { util.click(cancelBtn, "Modal Cancel"); }
    public void closeModal()        { util.click(closeBtn, "Modal Close"); }
    public String getBodyText()     { try { return modalBody.textContent().trim(); } catch (Exception e) { return ""; } }
}
