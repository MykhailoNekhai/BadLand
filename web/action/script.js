(function () {
    const statusText = document.getElementById("statusText");
    const statusDetail = document.getElementById("statusDetail");
    const statusIcon = document.getElementById("statusIcon");
    const params = new URLSearchParams(window.location.search);
    const mode = params.get("mode");

    const search = window.location.search || "";
    const routes = {
        verifyEmail: "../verify-email/" + search,
        resetPassword: "../reset-password/" + search,
        recoverEmail: "../change-email/" + search,
        revertSecondFactorAddition: "../change-email/" + search
    };

    const target = routes[mode];

    function fail(title, detail) {
        statusText.textContent = title;
        statusDetail.textContent = detail;
        if (statusIcon) {
            statusIcon.innerHTML = "!";
            statusIcon.classList.add("status-error");
        }
    }

    if (!mode || !params.get("oobCode")) {
        fail("Invalid action link.", "This link is missing required Firebase parameters.");
        return;
    }

    if (!target) {
        fail("Unsupported action.", "This Firebase action mode is not handled by this site yet.");
        return;
    }

    statusText.textContent = "Redirecting...";
    statusDetail.textContent = "Opening the correct account page now.";
    window.location.replace(target);
})();
