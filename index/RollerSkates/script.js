document.addEventListener('DOMContentLoaded', function () {
  const tabsContainer = document.getElementById('tabs');
  const iframe = document.getElementById('tabContent');

  const csvFileUrl = 'tabs.csv';  // CSV file to read from

  function parseCSV(data) {
    const rows = data.split('\n');
    rows.forEach((row, index) => {
      const [label, url] = row.split(',');
      if (label && url) {
        createTab(label, url.trim(), index);
      }
    });
  }

  function createTab(label, url, index) {
    const tabButton = document.createElement('button');
    tabButton.textContent = label;
    tabButton.classList.add('tab-button');
    tabButton.addEventListener('click', () => {
      iframe.src = url;
      setActiveTab(index);
    });
    tabsContainer.appendChild(tabButton);
  }

  function setActiveTab(activeIndex) {
    const buttons = document.querySelectorAll('.tab-button');
    buttons.forEach((button, index) => {
      button.classList.toggle('active', index === activeIndex);
    });
  }

  fetch(csvFileUrl)
    .then(response => response.text())
    .then(data => {
      parseCSV(data);
      setActiveTab(0); // Set the first tab active by default
    })
    .catch(error => console.error('Error loading CSV file:', error));
});
