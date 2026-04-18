# SayIt Studio — Installation Guide

Welcome! This guide walks you through setting up SayIt Studio from scratch.
No technical experience is required — just follow each step in order.

---

## Part 1: Prerequisites

Before you start, you need three things ready:
1. A GitHub account with a repository
2. A GitHub Personal Access Token (PAT)
3. The AIDE app on your Android phone

---

### Step 1.1 — Create a GitHub account (skip if you already have one)

1. Open a browser and go to **https://github.com**
2. Click **Sign up** in the top-right corner
3. Enter your email address, create a password, and choose a username
4. Verify your email address when GitHub sends you a confirmation

---

### Step 1.2 — Create a GitHub repository

1. Log in to **https://github.com**
2. Click the **+** icon in the top-right corner, then click **New repository**
3. Give it a name — for example: `sayit-studio`
4. Leave it set to **Public** (required for free GitHub Pages)
5. Check the box **"Add a README file"** — this ensures the repo is not empty
6. Click **Create repository**
7. Note down the exact repository name — you will need it shortly

---

### Step 1.3 — Create a Personal Access Token (PAT)

A PAT is like a special password that lets SayIt read and write files in your repo.

1. Log in to **https://github.com**
2. Click your profile picture in the top-right corner
3. Click **Settings**
4. Scroll all the way down the left sidebar and click **Developer settings**
5. Click **Personal access tokens**, then click **Fine-grained tokens**
6. Click **Generate new token**
7. Fill in the form:
   - **Token name:** type `sayit-studio`
   - **Expiration:** choose **No expiration** (or 1 year if you prefer)
   - **Repository access:** select **Only select repositories**, then choose your repo from the dropdown
8. Under **Repository permissions**, find **Contents** and set it to **Read and write**
9. Scroll down and click **Generate token**
10. **IMPORTANT:** Copy the token immediately — GitHub only shows it once!
    - It starts with `github_pat_`
    - Paste it somewhere safe (like a notes app) for now

---

### Step 1.4 — Install AIDE on your Android phone

1. Open the **Google Play Store** on your Android phone
2. Search for **AIDE**
3. Install **AIDE - Android IDE - Java, C++** (by appfour)
4. Open AIDE once to let it finish its initial setup

---

## Part 2: Run the Workbook

The workbook is an HTML file that generates all your SayIt files automatically.

---

### Step 2.1 — Open the workbook

1. On your phone (or any computer), open **Chrome** or any modern browser
2. Open the file **sayit-workbook.html** — you can do this by:
   - Tapping the file in your Downloads folder
   - Or navigating to it via the Files app and tapping "Open with Chrome"

---

### Step 2.2 — Fill in your details

The workbook will show a form. Fill in each field:

| Field | What to enter | Example |
|-------|---------------|---------|
| **GH Username** | Your GitHub username | `janesmith` |
| **GH Repo** | The repository name you created in Step 1.2 | `sayit-studio` |
| **PAT** | The token you copied in Step 1.3 | `github_pat_11A...` |
| **Subdir name** | A short folder name for this version | `say-it-v1` |
| **Display name** | What to call this workspace | `My SayIt` |

- The **Subdir name** is the folder inside your repo where your files will live. Use lowercase letters and hyphens only (no spaces). Examples: `say-it-v1`, `main-workspace`

---

### Step 2.3 — Generate the package

1. Tap **Generate Package** (or the equivalent button)
2. The workbook will create all your files
3. Wait for the generation to complete — usually a few seconds

---

### Step 2.4 — Download each file

1. The workbook will show download buttons for each file
2. Download all of the following files one by one:
   - `home.html`
   - `learn.html`
   - `do.html`
   - `fix.html`
   - `rollback.html`
   - `registry.json`
   - `profiles.json`
3. Make sure all files are saved to a folder you can find again (like your Downloads folder)

---

## Part 3: Deploy HTML Files to GitHub

Now you will upload all the files to your GitHub repository.

---

### Step 3.1 — Go to your repository

1. Open a browser and go to **https://github.com**
2. Click on your repository (`sayit-studio` or whatever you named it)

---

### Step 3.2 — Create the subfolder and upload files

GitHub doesn't let you create empty folders directly. The easiest way to upload files into a subfolder is:

1. In your repository, click **Add file** > **Upload files**
2. A file upload page appears
3. Before uploading, you need to set the folder path. Look at the URL bar — you will see:
   `github.com/yourusername/yourrepo/upload/main`
   — click on the `main` at the end and type after it:
   `main/say-it-v1` (replacing `say-it-v1` with your actual subdir name)
   
   **Easier alternative:** Use the GitHub web editor:
   - Click **Create new file**
   - In the filename box, type: `say-it-v1/home.html` (this automatically creates the folder)
   - Paste the contents of `home.html` into the editor
   - Repeat for each file — OR use the drag-and-drop upload method below

4. **Drag-and-drop upload (recommended):**
   - Go to your repo main page
   - Click **Add file** > **Upload files**
   - In the file path box at the top, click after `main/` and type your subdir name: `say-it-v1/`
   - Drag all 7 files (`home.html`, `learn.html`, `do.html`, `fix.html`, `rollback.html`, `registry.json`, `profiles.json`) into the upload box
   - All files should appear in the list

---

### Step 3.3 — Commit the files

1. Scroll down to the **Commit changes** section
2. Leave the commit message as-is or type: `initial upload`
3. Make sure **"Commit directly to the main branch"** is selected
4. Click **Commit changes**
5. Wait a moment — your files are now in the repository

---

### Step 3.4 — Verify the files are in the right place

1. Click on your repository name at the top to go back to the main page
2. You should see a folder named `say-it-v1` (or your chosen subdir)
3. Click into it — you should see all 7 files listed

---

### Step 3.5 — Enable GitHub Pages

GitHub Pages is the free hosting service that makes your files accessible as a website.

1. In your repository, click **Settings** (the gear icon, usually in the tabs near the top)
2. In the left sidebar, scroll down and click **Pages**
3. Under **Build and deployment**, set:
   - **Source:** Deploy from a branch
   - **Branch:** `main`
   - **Folder:** `/ (root)`
4. Click **Save**
5. A banner will appear saying "GitHub Pages source saved"
6. **Wait 2–3 minutes** for GitHub to build and publish your site

---

### Step 3.6 — Test your live site

1. After 2–3 minutes, go back to **Settings > Pages**
2. You should see a green banner with a URL like:
   `https://yourusername.github.io/sayit-studio/`
3. Visit this URL + your subdir + `/home.html`, for example:
   `https://yourusername.github.io/sayit-studio/say-it-v1/home.html`
4. If the page loads with the SayIt interface, your deployment is working

> If you see a 404 error, wait another 2 minutes and try again. GitHub Pages can take up to 5 minutes on the first deployment.

---

## Part 4: Set Up the APK in AIDE

Now you will build the Android app using AIDE on your phone.

---

### Step 4.1 — Create a new project in AIDE

1. Open **AIDE** on your phone
2. Tap **New Project**
3. Choose **Android App** (or **Android Application**)
4. Fill in:
   - **Application name:** `SayIt`
   - **Package name:** `com.acmeproducts.sayit`
   - **Minimum SDK:** `24`
5. Tap **Create**

---

### Step 4.2 — Replace MainActivity.java

1. In AIDE, you will see a file tree on the left
2. Navigate to: `src` > `com` > `acmeproducts` > `sayit`
3. Tap `MainActivity.java`
4. Tap the **menu icon** (three dots) > **Delete** to remove the default file
5. Tap the **+** icon to create a new file named `MainActivity.java`
6. Copy the entire contents of the `MainActivity.java` file you downloaded and paste it in
7. Save the file

---

### Step 4.3 — Set your Home URL

This is the most important step — you must tell the app where your HTML files are hosted.

1. In AIDE, open `MainActivity.java`
2. Find the line that reads:
   ```
   private static final String HOME_URL = "YOUR_URL_HERE";
   ```
3. Replace `YOUR_URL_HERE` with your actual URL, for example:
   ```
   private static final String HOME_URL = "https://yourusername.github.io/sayit-studio/say-it-v1/home.html";
   ```
4. Make sure the URL:
   - Starts with `https://`
   - Contains your actual GitHub username
   - Contains your actual repo name
   - Contains your actual subdir name
   - Ends with `/home.html`
5. Save the file

---

### Step 4.4 — Replace AndroidManifest.xml

1. In AIDE, find `AndroidManifest.xml` in the project root (the top-level folder)
2. Tap on it, select all the contents, and delete them
3. Paste the entire contents of the `AndroidManifest.xml` file you downloaded
4. Save the file

---

### Step 4.5 — Add the AppCompat dependency

SayIt uses the AppCompat library (which provides `AppCompatActivity`).

1. In AIDE, find `build.gradle` in the project root
2. Look for the `dependencies { }` section
3. Add this line inside the braces:
   ```
   implementation 'androidx.appcompat:appcompat:1.6.1'
   ```
4. The section should look like:
   ```
   dependencies {
       implementation 'androidx.appcompat:appcompat:1.6.1'
   }
   ```
5. Save the file

---

### Step 4.6 — Build the APK

1. Tap the **Run** button in AIDE (the triangle/play button, usually at the top)
2. AIDE will download dependencies and compile the code — this may take 2–5 minutes
3. If there are errors, check:
   - The HOME_URL line has no typos and is inside double quotes
   - The package name in AndroidManifest.xml matches: `com.acmeproducts.sayit`
   - The AppCompat dependency was added correctly
4. When the build succeeds, AIDE will ask to install the app
5. Tap **Install** and follow the prompts
6. If Android asks about "Install from unknown sources", tap **Settings** and enable it, then go back and install

---

## Part 5: First Launch

---

### Step 5.1 — Open the SayIt app

1. Find the **SayIt** icon in your app drawer
2. Tap to open it
3. It will load your `home.html` page from GitHub Pages

---

### Step 5.2 — Verify configuration

1. On the home screen, look for a **gear icon** (⚙) or **Settings** button
2. Tap it to open the config panel
3. Verify that your:
   - GitHub Username is correct
   - Repo name is correct
   - PAT is filled in
   - Subdir name matches what you uploaded (e.g. `say-it-v1`)
4. If any fields are empty, fill them in and save

---

### Step 5.3 — Train your first AI platform

1. Tap **Learn** on the home screen
2. Follow the instructions to set up your preferred AI platform (Claude, ChatGPT, etc.)
3. The app will open the AI platform in its second WebView
4. Log in to the AI platform if prompted

---

### Step 5.4 — Create your first project

1. Go back to the **Home** screen (tap the home icon or use the back button)
2. Tap the microphone button or say the wake phrase
3. Say something like:
   `"SayIt create my first project using Claude write a poem about nature"`
4. SayIt will send the prompt to Claude and capture the result

---

## Part 6: Adding More Versions

Over time, you may want to deploy an updated set of SayIt files (e.g. after a new release of the workbook).

---

### Step 6.1 — Run the workbook again with a new subdir

1. Open `sayit-workbook.html` again in your browser
2. Fill in all the same details, but change the **Subdir name** to something new:
   - For example: `say-it-v2`
3. Tap **Generate Package** and download the new files

---

### Step 6.2 — Upload new files to GitHub

1. Follow the same steps as Part 3
2. Upload all the new files to the **new subfolder** (e.g. `say-it-v2/`)
3. The old `say-it-v1/` folder remains intact — your existing projects are safe

---

### Step 6.3 — Switch to the new version in the app

1. Open the SayIt app
2. Tap the **gear icon** on the home screen
3. Change the **Subdir** field from `say-it-v1` to `say-it-v2`
4. Save the config
5. The app will reload using the new version

> You do NOT need to reinstall the APK. The subdir switch happens entirely in the HTML configuration. The Java code just uses whatever subdir the HTML page tells it to use.

---

## Troubleshooting

### Home.html shows a 404 error

- Double-check the URL in `MainActivity.java` — every character must be exact
- Make sure GitHub Pages is enabled in your repo Settings > Pages
- Wait a few more minutes — the first deployment can take up to 10 minutes
- Check that your files are in the correct folder (e.g. `say-it-v1/home.html` not `home.html`)

---

### Voice recognition is not working

- Make sure you have granted **microphone permission** to the SayIt app
  - Go to Android **Settings** > **Apps** > **SayIt** > **Permissions** > **Microphone** > Allow
- If using Chrome inside the app, also check that Chrome has microphone permission
- Speak clearly and wait for the listening indicator before speaking

---

### The AI platform won't open / shows a blank screen

- Make sure you are logged into Claude/ChatGPT/Gemini etc. in the app's second WebView
  - Tap **Learn** and log in to the platform manually
- Check your internet connection
- Some AI platforms occasionally change their URLs — check the `PLATFORM_URLS` map in `MainActivity.java`

---

### PAT is rejected / API calls fail

- Your PAT may have expired — go back to GitHub Settings > Developer settings > Fine-grained tokens and create a new one
- Make sure the PAT has **Contents: Read and write** permission for your repository
- Check there are no extra spaces before or after the PAT when you paste it into the config

---

### AIDE build fails with errors

- Make sure the `dependencies` block in `build.gradle` is correct (see Step 4.5)
- Check that `package com.acmeproducts.sayit;` is the first line in `MainActivity.java`
- Try tapping **File** > **Sync** or **Tools** > **Clean Project** in AIDE, then build again
- Ensure you have a stable internet connection — AIDE downloads libraries during the first build

---

### App installs but crashes on startup

- The most common cause is `HOME_URL` still set to `"YOUR_URL_HERE"` — make sure you replaced it with your real URL
- Check the URL doesn't have any extra spaces or line breaks inside the quotes
- Make sure the URL starts with `https://` not `http://`

---

*SayIt Studio — built for non-technical users who want AI superpowers.*
