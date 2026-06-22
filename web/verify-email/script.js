(() => {
    "use strict";

    const icon = document.getElementById("statusIcon");
    const text = document.getElementById("statusText");
    const detail = document.getElementById("statusDetail");

    const params = new URLSearchParams(window.location.search);
    const oobCode = params.get("oobCode");

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

    function setSuccess(title, sub) {
        icon.classList.remove("error");
        icon.classList.add("success");
        icon.innerHTML = ICON_SUCCESS;
        text.textContent = title;
        detail.textContent = sub;
    }

    function setError(title, sub) {
        icon.classList.remove("success");
        icon.classList.add("error");
        icon.innerHTML = ICON_ERROR;
        text.textContent = title;
        detail.textContent = sub;
    }

    async function fakeApplyActionCode(code) {
        // STUB: pretend to call applyActionCode(auth, oobCode) on Firebase.
        // Replace with real Firebase auth integration later.
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (!code) {
                    reject(new Error("missing-code"));
                    return;
                }
                resolve();
            }, 900);
        });
    }

    (async () => {
        try {
            await fakeApplyActionCode(oobCode);
            setSuccess(
                "Account verified",
                "You can now return to the game and sign in."
            );
        } catch (e) {
            setError(
                "Link expired or invalid",
                "Request a new verification email from the game."
            );
        }
    })();
})();
