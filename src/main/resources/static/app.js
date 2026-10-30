const $ = (id) => document.getElementById(id);
let currentAccount = null;

async function api(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: { "Content-Type": "application/json", ...(options.headers || {}) }
  });
  const body = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(body.error || body.message || `Request failed (${response.status})`);
  return body;
}
function showMessage(id, text, isError = false) {
  const node = $(id); node.textContent = text; node.classList.toggle("error", isError);
}
function money(value) {
  return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" }).format(Number(value || 0));
}
function date(value) {
  return value ? new Date(value).toLocaleString("en-IN") : "";
}
function renderAccount(account) {
  currentAccount = account;
  $("account-id").value = account.id;
  const card = $("account-card");
  card.classList.remove("hidden");
  card.innerHTML = `<div class="account-top"><div><div class="account-name"></div><div class="account-number"></div></div><span class="step">ACTIVE</span></div><div class="balance-label">AVAILABLE BALANCE</div><div class="balance"></div>`;
  card.querySelector(".account-name").textContent = account.accountHolder;
  card.querySelector(".account-number").textContent = `${account.accountNumber} · ${account.email}`;
  card.querySelector(".balance").textContent = money(account.balance);
}
async function loadAccount() {
  const id = $("account-id").value;
  if (!id) return showMessage("operation-message", "Enter an account ID first.", true);
  try {
    renderAccount(await api(`/api/accounts/${id}`));
    await loadHistory();
    showMessage("operation-message", "Account loaded successfully.");
  } catch (e) { showMessage("operation-message", e.message, true); }
}
async function loadHistory() {
  if (!currentAccount) return;
  const list = await api(`/api/accounts/${currentAccount.id}/transactions`);
  const history = $("history");
  if (!list.length) { history.className = "history empty"; history.textContent = "No transactions yet."; return; }
  history.className = "history";
  history.replaceChildren();
  list.forEach(tx => {
    const row = document.createElement("div"); row.className = "transaction";
    const left = document.createElement("div");
    const title = document.createElement("strong"); title.textContent = tx.type;
    const small = document.createElement("small"); small.textContent = `${date(tx.createdAt)} · ${tx.description || ""}`;
    left.append(title, small);
    const amount = document.createElement("span");
    amount.className = tx.type === "DEPOSIT" ? "amount-in" : "amount-out";
    amount.textContent = `${tx.type === "DEPOSIT" ? "+" : "−"}${money(tx.amount)}`;
    row.append(left, amount); history.append(row);
  });
}
$("create-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  try {
    const account = await api("/api/accounts", { method: "POST", body: JSON.stringify({ accountHolder: form.get("accountHolder"), email: form.get("email") }) });
    renderAccount(account);
    await loadHistory();
    showMessage("create-message", `Account created. Your account ID is ${account.id}. Save this ID for the demo.`);
  } catch (e) { showMessage("create-message", e.message, true); }
});
$("load-account").addEventListener("click", loadAccount);
$("refresh-history").addEventListener("click", async () => {
  try { await loadHistory(); showMessage("operation-message", "Transaction history refreshed."); }
  catch (e) { showMessage("operation-message", e.message, true); }
});
for (const [formId, action] of [["deposit-form", "deposit"], ["withdraw-form", "withdraw"]]) {
  $(formId).addEventListener("submit", async (event) => {
    event.preventDefault();

    // CHANGE 1: Save the form reference before using await.
    const formElement = event.currentTarget;

    if (!currentAccount) {
      return showMessage("operation-message", "Load or create an account first.", true);
    }

    const amount = Number(new FormData(formElement).get("amount"));

    try {
      const account = await api(`/api/accounts/${currentAccount.id}/${action}`, {
        method: "POST",
        body: JSON.stringify({ amount })
      });

      renderAccount(account);
      await loadHistory();

      showMessage(
        "operation-message",
        `${action === "deposit" ? "Deposit" : "Withdrawal"} completed successfully.`
      );

      // CHANGE 2: Reset the saved form reference.
      formElement.reset();

    } catch (e) {
      showMessage("operation-message", e.message, true);
    }
  });
}
