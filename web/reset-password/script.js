(() => {
    "use strict";

    const form = document.getElementById("resetForm");
    const newPassword = document.getElementById("newPassword");
    const repeatPassword = document.getElementById("repeatPassword");
    const message = document.getElementById("formMessage");
    const submitBtn = document.getElementById("submitBtn");
    const submitLabel = submitBtn.querySelector(".btn-label");

    const MIN_LEN = 8;

    function setMessage(text, kind) {
        message.textContent = text || "";
        message.classList.remove("success", "error");
        if (kind) message.classList.add(kind);
    }

    function validate() {
        const a = newPassword.value;
        const b = repeatPassword.value;

        if (a.length < MIN_LEN) {
            return `Password must be at least ${MIN_LEN} characters.`;
        }
        if (a !== b) {
            return "Passwords do not match.";
        }
        return null;
    }

    [newPassword, repeatPassword].forEach((el) => {
        el.addEventListener("input", () => {
            if (message.textContent) setMessage("");
        });
    });

    async function fakeBackendReset(password) {
        // STUB: pretend to talk to Firebase Auth.
        // Real impl: confirmPasswordReset(auth, oobCode, password)
        return new Promise((resolve) => setTimeout(resolve, 900));
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const err = validate();
        if (err) {
            setMessage(err, "error");
            return;
        }

        submitBtn.disabled = true;
        const original = submitLabel.textContent;
        submitLabel.textContent = "RESETTING...";

        try {
            await fakeBackendReset(newPassword.value);
            submitLabel.textContent = "PASSWORD RESET";
            setMessage("Your password has been updated.", "success");
            form.reset();
        } catch (ex) {
            setMessage("Something went wrong. Please try again.", "error");
            submitLabel.textContent = original;
            submitBtn.disabled = false;
            return;
        }

        setTimeout(() => {
            submitLabel.textContent = original;
            submitBtn.disabled = false;
        }, 2200);
    });
})();
