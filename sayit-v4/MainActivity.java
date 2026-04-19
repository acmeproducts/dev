// ═══════════════════════════════════════════════════════════════════════════════
// SayIt Studio - MainActivity.java
// Package: com.acmeproducts.sayit
//
// HOW TO USE THIS FILE IN AIDE:
// 1. Open your AIDE project
// 2. Navigate to src/com/acmeproducts/sayit/
// 3. Replace the existing MainActivity.java with this file
// 4. Find the line that says HOME_URL = "https://acmeproducts.github.io/dev/sayit-v4/home.html" (around line 50)
// 5. Replace https://acmeproducts.github.io/dev/sayit-v4/home.html with your actual GitHub Pages URL, e.g.:
//    https://myusername.github.io/my-repo/say-it-v1/home.html
// 6. Save and build the project
// ═══════════════════════════════════════════════════════════════════════════════

package com.acmeproducts.sayit;

// ── Standard Android imports ─────────────────────────────────────────────────
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

// ── AndroidX / AppCompat ─────────────────────────────────────────────────────
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// ── Java utilities ────────────────────────────────────────────────────────────
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // ── TAG for log output (visible in Android Logcat) ───────────────────────
    private static final String TAG = "SayIt";

    // ── HOME URL ─────────────────────────────────────────────────────────────
    // IMPORTANT: Replace https://acmeproducts.github.io/dev/sayit-v4/home.html with your actual GitHub Pages home URL.
    // Example: "https://myusername.github.io/my-repo/say-it-v1/home.html"
    private static final String HOME_URL = "https://acmeproducts.github.io/dev/sayit-v4/home.html";

    // ── Known AI platform URLs ────────────────────────────────────────────────
    // These are the AI sites that open inside the second WebView (runtime view).
    // You can add more platforms here if needed.
    private static final Map<String, String> PLATFORM_URLS = new HashMap<String, String>() {{
        put("claude",      "https://claude.ai/new");
        put("chatgpt",     "https://chatgpt.com");
        put("gemini",      "https://gemini.google.com");
        put("perplexity",  "https://www.perplexity.ai");
        put("codex",       "https://codex.openai.com");
    }};

    // ── Permission request codes ──────────────────────────────────────────────
    private static final int PERM_CLIPBOARD = 101;
    private static final int PERM_AUDIO     = 102;

    // ── SharedPreferences key ─────────────────────────────────────────────────
    // SayIt config is stored here so Java can inject it into the WebView.
    private static final String PREFS_NAME = "SayItConfig";

    // ── Two WebViews ──────────────────────────────────────────────────────────
    // sayitWebView  → shows your GitHub Pages HTML (home, learn, do, fix, rollback)
    // runtimeWebView → shows the AI platform (Claude, ChatGPT, etc.)
    private WebView sayitWebView;
    private WebView runtimeWebView;

    // ── Root layout that holds both WebViews ──────────────────────────────────
    private FrameLayout rootLayout;

    // ── Track which WebView is currently visible ──────────────────────────────
    // true = SayIt HTML is in front, false = AI platform is in front
    private boolean sayitOnTop = true;

    // ════════════════════════════════════════════════════════════════════════
    // onCreate — Called when the app first starts
    // ════════════════════════════════════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ── Build the layout in code (no XML needed) ─────────────────────────
        // FrameLayout lets both WebViews sit on top of each other like layers.
        rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        setContentView(rootLayout);

        // ── Create and configure both WebViews ───────────────────────────────
        sayitWebView   = createConfiguredWebView(true);
        runtimeWebView = createConfiguredWebView(false);

        // Add both to the FrameLayout; SayIt goes on top (added second)
        rootLayout.addView(runtimeWebView, new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootLayout.addView(sayitWebView, new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // ── Load the home page ────────────────────────────────────────────────
        sayitWebView.loadUrl(HOME_URL);

        // ── Pre-load Claude so it's ready when the user switches ─────────────
        runtimeWebView.loadUrl(PLATFORM_URLS.get("claude"));

        // ── Request audio permission (needed for Web Speech API in Chrome) ────
        requestAudioPermissionIfNeeded();

        Log.i(TAG, "SayIt started. Home URL: " + HOME_URL);
    }

    // ════════════════════════════════════════════════════════════════════════
    // createConfiguredWebView — Sets up a WebView with all required settings
    // isSayit: true for the SayIt HTML WebView, false for the AI platform
    // ════════════════════════════════════════════════════════════════════════
    private WebView createConfiguredWebView(boolean isSayit) {
        WebView webView = new WebView(this);

        // ── WebSettings: enable everything the HTML app needs ─────────────────
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);         // Must be on for any web app
        settings.setDomStorageEnabled(true);          // Enables localStorage
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);            // Allow file:// URLs
        settings.setAllowContentAccess(true);
        settings.setGeolocationEnabled(false);        // Not needed
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMediaPlaybackRequiresUserGesture(false); // Auto speech playback
        settings.setUserAgentString(settings.getUserAgentString() + " SayItApp/1.0");

        // Allow mixed content (http inside https) — needed for some AI platforms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // ── Hardware acceleration ─────────────────────────────────────────────
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);

        // ── Inject the JavaScript bridge on the SayIt WebView only ───────────
        if (isSayit) {
            webView.addJavascriptInterface(new SayItBridgeInterface(), "SayItBridgeInterface");
            Log.i(TAG, "JavaScript bridge attached to SayIt WebView");
        }

        // ── WebViewClient: controls navigation behaviour ──────────────────────
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, (isSayit ? "[SayIt] " : "[Runtime] ") + "Loading: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // ── Inject config from SharedPreferences into SayIt WebView ──
                // This is how Android config (PAT, username, etc.) reaches the HTML.
                if (isSayit) {
                    injectConfigIntoWebView(view);
                }

                Log.d(TAG, (isSayit ? "[SayIt] " : "[Runtime] ") + "Loaded: " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                // ── Keep GitHub Pages URLs inside the SayIt WebView ──────────
                if (isSayit) {
                    if (url.contains("github.io") || url.contains("github.com/api")) {
                        return false; // Stay inside WebView
                    }
                }

                // ── Keep AI platform URLs inside the Runtime WebView ──────────
                for (Map.Entry<String, String> entry : PLATFORM_URLS.entrySet()) {
                    if (url.startsWith(entry.getValue()) ||
                        url.contains(entry.getKey() + ".ai") ||
                        url.contains(entry.getKey() + ".com")) {
                        if (!isSayit) return false; // Stay inside runtime WebView
                        // If SayIt WebView gets an AI URL, send it to runtime
                        runtimeWebView.loadUrl(url);
                        bringRuntimeToFront();
                        return true;
                    }
                }

                // ── For anything else, open the system browser ─────────────
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Could not open URL: " + url, e);
                }
                return true; // We handled it
            }
        });

        // ── WebChromeClient: handles JS alerts, console, and permissions ──────
        webView.setWebChromeClient(new WebChromeClient() {

            // Allow microphone permission for Web Speech API
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                String[] grantedResources = {};
                for (String resource : request.getResources()) {
                    if (resource.equals(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                        grantedResources = new String[]{ PermissionRequest.RESOURCE_AUDIO_CAPTURE };
                    }
                }
                request.grant(grantedResources);
            }

            // Forward JS console.log/warn/error to Android Logcat
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG + "_JS", consoleMessage.message() +
                      " [" + consoleMessage.sourceId() + ":" + consoleMessage.lineNumber() + "]");
                return true;
            }

            // Support window.alert() in the WebView
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     android.webkit.JsResult result) {
                new android.app.AlertDialog.Builder(MainActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK", (d, w) -> result.confirm())
                    .setOnCancelListener(d -> result.cancel())
                    .create().show();
                return true;
            }

            // Support window.confirm()
            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       android.webkit.JsResult result) {
                new android.app.AlertDialog.Builder(MainActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK",     (d, w) -> result.confirm())
                    .setNegativeButton("Cancel", (d, w) -> result.cancel())
                    .setOnCancelListener(d -> result.cancel())
                    .create().show();
                return true;
            }
        });

        return webView;
    }

    // ════════════════════════════════════════════════════════════════════════
    // injectConfigIntoWebView
    // Reads saved config from Android SharedPreferences and injects it as
    // localStorage keys into the WebView via JavaScript.
    // This means the user only has to configure once — the APK remembers it.
    // ════════════════════════════════════════════════════════════════════════
    private void injectConfigIntoWebView(WebView view) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // NOTE: PAT is intentionally NOT read from SharedPreferences.
        // The PAT lives only in the HTML localStorage — it is set once by the
        // first-load bootstrap script embedded in each generated HTML file,
        // and persisted by the WebView's own localStorage across sessions.
        // This means PAT never touches Android storage and does not need to
        // be rotated by reinstalling the APK.
        String ghUser  = prefs.getString("SAYIT_GH_USER",  "");
        String ghRepo  = prefs.getString("SAYIT_GH_REPO",  "");
        String subdir  = prefs.getString("SAYIT_SUBDIR",   "");
        String name    = prefs.getString("SAYIT_DISPLAY_NAME", "");
        String wake    = prefs.getString("SAYIT_WAKE_WORD",  "");

        // Only inject if we actually have values — don't overwrite with empty
        if (!ghUser.isEmpty() || !ghRepo.isEmpty()) {
            String js = "javascript:(function(){"
                + "try {"
                + "if('" + escapeForJs(ghUser)   + "'){localStorage.setItem('SAYIT_GH_USER','"      + escapeForJs(ghUser)   + "');}"
                + "if('" + escapeForJs(ghRepo)   + "'){localStorage.setItem('SAYIT_GH_REPO','"      + escapeForJs(ghRepo)   + "');}"
                + "if('" + escapeForJs(subdir)   + "'){localStorage.setItem('SAYIT_SUBDIR','"       + escapeForJs(subdir)   + "');}"
                + "if('" + escapeForJs(name)     + "'){localStorage.setItem('SAYIT_DISPLAY_NAME','" + escapeForJs(name)     + "');}"
                + "if('" + escapeForJs(wake)     + "'){localStorage.setItem('SAYIT_WAKE_WORD','"    + escapeForJs(wake)     + "');}"
                + "console.log('SayIt: config injected from Android prefs (PAT handled by HTML)');"
                + "} catch(e){ console.warn('SayIt inject error:', e); }"
                + "})();";
            view.loadUrl(js);
            Log.d(TAG, "Config injected into WebView (user=" + ghUser + ", repo=" + ghRepo + ") — PAT not in prefs by design");
        }
    }

    // Escape single quotes so they don't break JavaScript string literals
    private String escapeForJs(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n");
    }

    // ════════════════════════════════════════════════════════════════════════
    // WebView switching helpers
    // ════════════════════════════════════════════════════════════════════════

    // Bring the AI platform WebView to the front (user is now in Claude/ChatGPT)
    private void bringRuntimeToFront() {
        sayitOnTop = false;
        sayitWebView.bringToFront();   // Paradoxically: bring sayit BEHIND
        runtimeWebView.bringToFront(); // Runtime comes on top
        rootLayout.invalidate();
        Log.d(TAG, "Runtime WebView is now in front");
    }

    // Bring the SayIt HTML WebView back to the front
    private void bringHomToFront() {
        sayitOnTop = true;
        runtimeWebView.bringToFront(); // Send runtime behind
        sayitWebView.bringToFront();   // SayIt on top
        rootLayout.invalidate();
        Log.d(TAG, "SayIt WebView is now in front");
    }

    // ════════════════════════════════════════════════════════════════════════
    // SayItBridgeInterface — The JavaScript ↔ Java bridge
    // Methods here are callable from HTML via: window.SayItBridgeInterface.methodName(...)
    // Each method is annotated @JavascriptInterface for security.
    // ════════════════════════════════════════════════════════════════════════
    public class SayItBridgeInterface {

        // ── readClipboard ─────────────────────────────────────────────────────
        // Called from JS to read whatever text is on the Android clipboard.
        // Returns the clipboard text, or empty string if nothing is there.
        @JavascriptInterface
        public String readClipboard() {
            try {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm == null || !cm.hasPrimaryClip()) return "";
                ClipData.Item item = cm.getPrimaryClip().getItemAt(0);
                CharSequence text = item.getText();
                return text != null ? text.toString() : "";
            } catch (Exception e) {
                Log.e(TAG, "readClipboard error", e);
                return "";
            }
        }

        // ── writeClipboard ────────────────────────────────────────────────────
        // Called from JS to put text onto the Android clipboard.
        // text: the string to copy
        @JavascriptInterface
        public void writeClipboard(String text) {
            try {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm == null) return;
                ClipData clip = ClipData.newPlainText("SayIt", text);
                cm.setPrimaryClip(clip);
                Log.d(TAG, "Clipboard written: " + text.length() + " chars");
            } catch (Exception e) {
                Log.e(TAG, "writeClipboard error", e);
            }
        }

        // ── openUrl ───────────────────────────────────────────────────────────
        // Called from JS to load a URL in the AI platform WebView (runtime view).
        // url: the full URL to open, e.g. "https://claude.ai/new"
        @JavascriptInterface
        public void openUrl(final String url) {
            Log.d(TAG, "openUrl: " + url);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runtimeWebView.loadUrl(url);
                    bringRuntimeToFront();
                }
            });
        }

        // ── focusRuntime ──────────────────────────────────────────────────────
        // Called from JS with a platform name like "claude" or "chatgpt".
        // Looks up the known URL and opens it in the runtime WebView.
        // platform: one of claude, chatgpt, gemini, perplexity, codex
        @JavascriptInterface
        public void focusRuntime(final String platform) {
            Log.d(TAG, "focusRuntime: " + platform);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String url = PLATFORM_URLS.get(platform.toLowerCase());
                    if (url == null) {
                        // If unknown platform, try using it directly as a URL
                        url = platform.startsWith("http") ? platform : "https://" + platform;
                    }
                    runtimeWebView.loadUrl(url);
                    bringRuntimeToFront();
                }
            });
        }

        // ── captureRuntime ────────────────────────────────────────────────────
        // Injects JavaScript into the AI platform WebView to extract content
        // (code blocks or the largest text block), then sends it back to the
        // SayIt HTML via window.receiveEvent().
        @JavascriptInterface
        public void captureRuntime() {
            Log.d(TAG, "captureRuntime called");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This JS runs inside the AI platform WebView (Claude, ChatGPT, etc.)
                    String extractJs =
                        "(function() {" +
                        "  try {" +
                        // Step 1: Look for code blocks (pre > code tags)
                        "    var codeEls = document.querySelectorAll('pre code, pre, code');" +
                        "    var bestCode = '';" +
                        "    var bestLen = 0;" +
                        "    for (var i = 0; i < codeEls.length; i++) {" +
                        "      var t = codeEls[i].innerText || codeEls[i].textContent || '';" +
                        "      if (t.length > bestLen) { bestCode = t; bestLen = t.length; }" +
                        "    }" +
                        "    if (bestLen > 50) return bestCode;" +

                        // Step 2: Fall back to the largest text block
                        "    var paras = document.querySelectorAll('p, div, article, section, [class*=\"message\"], [class*=\"response\"]');" +
                        "    var bestText = '';" +
                        "    bestLen = 0;" +
                        "    for (var j = 0; j < paras.length; j++) {" +
                        "      var txt = paras[j].innerText || paras[j].textContent || '';" +
                        "      txt = txt.trim();" +
                        "      if (txt.length > bestLen && txt.length < 50000) {" +
                        "        bestText = txt; bestLen = txt.length;" +
                        "      }" +
                        "    }" +
                        "    return bestText || document.body.innerText || '';" +
                        "  } catch(e) { return 'Error: ' + e.message; }" +
                        "})()";

                    runtimeWebView.evaluateJavascript(extractJs, new android.webkit.ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            // value comes back as a JSON-quoted string from evaluateJavascript
                            // Remove outer quotes and unescape
                            String content = value;
                            if (content != null && content.startsWith("\"")) {
                                content = content.substring(1, content.length() - 1)
                                                 .replace("\\n", "\n")
                                                 .replace("\\t", "\t")
                                                 .replace("\\\"", "\"")
                                                 .replace("\\\\", "\\");
                            }

                            // Escape for embedding in a JS string literal
                            final String escaped = content != null
                                ? content.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")
                                : "";

                            // Send result back to the SayIt HTML WebView
                            final String sendBackJs =
                                "javascript:(function(){" +
                                "try{" +
                                "window.receiveEvent(JSON.stringify({type:'capture_result',content:'" + escaped + "'}));" +
                                "}catch(e){console.error('receiveEvent error',e);}" +
                                "})();";

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sayitWebView.loadUrl(sendBackJs);
                                    Log.d(TAG, "captureRuntime result sent back to SayIt WebView");
                                }
                            });
                        }
                    });
                }
            });
        }

        // ── appState ──────────────────────────────────────────────────────────
        // Called from JS to check whether the native bridge is available and
        // what capabilities the app has. Returns a JSON string.
        @JavascriptInterface
        public String appState() {
            return "{\"bridgeAvailable\":true,\"clipboardAvailable\":true,\"version\":\"1.0\"}";
        }

        // ── switchToHome ──────────────────────────────────────────────────────
        // Called from JS (e.g. from a "Back" button in the runtime view)
        // to bring the SayIt HTML WebView back to the foreground.
        @JavascriptInterface
        public void switchToHome() {
            Log.d(TAG, "switchToHome called");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bringHomToFront();
                }
            });
        }

        // ── saveConfig ────────────────────────────────────────────────────────
        // Called from the SayIt HTML config page to persist non-sensitive
        // settings into Android SharedPreferences so they survive app restarts.
        // PAT is explicitly blocked — it never enters Android storage.
        // PAT lives only in the WebView's own localStorage.
        @JavascriptInterface
        public void saveConfig(String key, String value) {
            // Block PAT from ever being written to Android SharedPreferences
            if ("SAYIT_GH_PAT".equals(key)) {
                Log.d(TAG, "saveConfig: PAT blocked from Android prefs by design — stays in HTML localStorage only");
                return;
            }
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(key, value);
            editor.apply();
            Log.d(TAG, "Config saved: " + key + " = [hidden]");
        }

        // ── getConfig ─────────────────────────────────────────────────────────
        // Called from the SayIt HTML to read a saved config value.
        // PAT is blocked — it is only accessible from HTML localStorage directly.
        @JavascriptInterface
        public String getConfig(String key) {
            // Block PAT from being read via the bridge — use localStorage in HTML directly
            if ("SAYIT_GH_PAT".equals(key)) {
                Log.d(TAG, "getConfig: PAT blocked from bridge read by design");
                return "";
            }
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return prefs.getString(key, "");
        }

        // ── showToast ─────────────────────────────────────────────────────────
        // Convenience method to show an Android Toast notification.
        // message: short text to display
        @JavascriptInterface
        public void showToast(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // onKeyDown — Handle the Android back button
    // If the AI platform is in front → go back to SayIt HTML
    // If SayIt WebView has history → go back in browser history
    // Otherwise → exit the app
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!sayitOnTop) {
                // User was in the AI platform — go back to SayIt
                bringHomToFront();
                return true;
            }
            if (sayitWebView.canGoBack()) {
                sayitWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Permission handling
    // Android 13+ requires explicit permission to read clipboard.
    // We request audio for the Web Speech API (voice recognition).
    // ════════════════════════════════════════════════════════════════════════
    private void requestAudioPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{ android.Manifest.permission.RECORD_AUDIO },
                PERM_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Audio permission granted");
            } else {
                Log.w(TAG, "Audio permission denied — voice recognition may not work");
                Toast.makeText(this,
                    "Microphone permission is needed for voice recognition.",
                    Toast.LENGTH_LONG).show();
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Lifecycle — Pause/Resume WebViews with the Activity
    // ════════════════════════════════════════════════════════════════════════
    @Override
    protected void onResume() {
        super.onResume();
        if (sayitWebView   != null) sayitWebView.onResume();
        if (runtimeWebView != null) runtimeWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sayitWebView   != null) sayitWebView.onPause();
        if (runtimeWebView != null) runtimeWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sayitWebView   != null) { sayitWebView.destroy();   sayitWebView   = null; }
        if (runtimeWebView != null) { runtimeWebView.destroy(); runtimeWebView = null; }
    }
}
