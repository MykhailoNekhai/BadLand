(() => {
    "use strict";

    const confirmStage = document.getElementById("confirmStage");
    const resultStage = document.getElementById("resultStage");
    const newEmailLabel = document.getElementById("newEmailLabel");
    const confirmBtn = document.getElementById("confirmBtn");
    const confirmLabel = confirmBtn.querySelector(".btn-label");
    const message = document.getElementById("formMessage");
    const statusIcon = document.getElementById("statusIcon");
    const statusText = document.getElementById("statusText");
    const statusDetail = document.getElementById("statusDetail");

    const params = new URLSearchParams(window.location.search);
    const oobCode = params.get("oobCode");
    const newEmail = params.get("newEmail") || "(not provided)";

    newEmailLabel.textContent = newEmail;

    const ICON_SUCCESS = `
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="9"/>
            <path d="m8 12 3 3 5-6"/>
        </svg>`;

    const ICON_ERROR = `
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="9"/>
            <path d="m9 9 6 6m0-6-6 6"/>
        </svg>`;

    function setMessage(text, kind) {
        message.textContent = text || "";
        message.classList.remove("success", "error");
        if (kind) message.classList.add(kind);
    }

    function showResult(kind, title, detail) {
        confirmStage.hidden = true;
        resultStage.hidden = false;
        if (kind === "success") {
            statusIcon.classList.add("success");
            statusIcon.innerHTML = ICON_SUCCESS;
        } else {
            statusIcon.classList.add("error");
            statusIcon.innerHTML = ICON_ERROR;
        }
        statusText.textContent = title;
        statusDetail.textContent = detail;
    }

    async function fakeConfirmEmailChange(code) {
        // STUB: pretend to call applyActionCode(auth, oobCode) for an email update.
        // Real impl: applyActionCode(getAuth(), oobCode) or updateEmail(user, newEmail)
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (!code) reject(new Error("missing-code"));
                else resolve();
            }, 900);
        });
    }

    confirmBtn.addEventListener("click", async () => {
        confirmBtn.disabled = true;
        const original = confirmLabel.textContent;
        confirmLabel.textContent = "UPDATING...";
        setMessage("");

        try {
            await fakeConfirmEmailChange(oobCode);
            showResult(
                "success",
                "Email updated",
                "Your account email has been changed to " + newEmail + "."
            );
        } catch (e) {
            showResult(
                "error",
                "Link expired or invalid",
                "Request a new email change from the game."
            );
        }
    });
})();
