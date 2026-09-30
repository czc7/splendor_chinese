import { expect, test } from "@playwright/test";

test("rooms show their number, turn time, and creator delete control", async ({ page }) => {
    await page.addInitScript(() => {
        localStorage.setItem("username", "user1");
        localStorage.setItem("accessToken", "test-token");
    });

    await page.route("**/oauth/username**", route => route.fulfill({ body: "user1" }));
    await page.route("**/api/users/user1**", route => route.fulfill({
        contentType: "application/json",
        body: JSON.stringify({ name: "user1", preferredColour: "00DD44", role: "ROLE_PLAYER" })
    }));
    await page.route("**/api/sessions?hash=**", route => route.fulfill({
        contentType: "application/json",
        body: JSON.stringify({
            sessions: {
                "731204": {
                    creator: "user1",
                    players: ["user1", "user3"],
                    launched: true,
                    savegameid: "",
                    turnTimeSeconds: 120,
                    gameParameters: {
                        name: "splendor_BASE",
                        displayName: "璀璨宝石：经典版",
                        maxSessionPlayers: 4
                    }
                }
            }
        })
    }));

    await page.goto("/lobby/");

    await expect(page.getByRole("columnheader", { name: "房间号" })).toBeVisible();
    await expect(page.getByRole("columnheader", { name: "回合限时" })).toBeVisible();
    const room = page.locator('tr[session-id="731204"]');
    await expect(room.locator(".session-id")).toHaveText("731204");
    await expect(room.locator(".session-turn-time")).toHaveText("每回合 120 秒");
    await expect(room.locator(".del-btn")).toBeVisible();
});
