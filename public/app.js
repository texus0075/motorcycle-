// MotoScope Web Application Core Logic
const getGeminiKey = () => atob("QVEuQWI4Uk42S1E3clhXT3JBSHBpaXlWVFoydzBRWjRSRmNwanZVanNNQXJ5akw1UHpZeUE=");

// Motorcycle Catalog Dataset
const MOTORCYCLES = [
  {
    id: "yamaha-r15-v4",
    name: "Yamaha YZF-R15 V4",
    brand: "Yamaha",
    category: "Sport",
    year: 2024,
    priceInr: 182000,
    priceDisplay: "₹1,82,000 ($2,200)",
    powerHp: 18.4,
    engineCc: 155,
    mileage: 46.0,
    weightKg: 142,
    topSpeed: 142,
    seatHeightMm: 815,
    rating: 4.8,
    img: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
    desc: "Liquid-cooled 4-valve VVA, Deltabox frame, Assist & Slipper clutch, Dual-Channel ABS."
  },
  {
    id: "ktm-rc-200",
    name: "KTM RC 200",
    brand: "KTM",
    category: "Super Sport",
    year: 2024,
    priceInr: 218000,
    priceDisplay: "₹2,18,000 ($2,650)",
    powerHp: 25.0,
    engineCc: 199,
    mileage: 35.0,
    weightKg: 151,
    topSpeed: 145,
    seatHeightMm: 824,
    rating: 4.6,
    img: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
    desc: "Lightweight trellis frame, WP APEX inverted suspension, ByBre 320mm front disc, Supermoto ABS."
  },
  {
    id: "kawasaki-ninja-300",
    name: "Kawasaki Ninja 300",
    brand: "Kawasaki",
    category: "Sport",
    year: 2024,
    priceInr: 343000,
    priceDisplay: "₹3,43,000 ($4,150)",
    powerHp: 39.0,
    engineCc: 296,
    mileage: 30.0,
    weightKg: 179,
    topSpeed: 175,
    seatHeightMm: 785,
    rating: 4.7,
    img: "https://images.unsplash.com/photo-1591637333184-19aa84b3e01f?auto=format&fit=crop&w=800&q=80",
    desc: "Parallel-twin 8-valve DOHC screamer, race-derived slipper clutch, manageable 785mm seat height."
  },
  {
    id: "triumph-speed-400",
    name: "Triumph Speed 400",
    brand: "Triumph",
    category: "Naked",
    year: 2024,
    priceInr: 233000,
    priceDisplay: "₹2,33,000 ($2,850)",
    powerHp: 40.0,
    engineCc: 398,
    mileage: 29.0,
    weightKg: 170,
    topSpeed: 158,
    seatHeightMm: 790,
    rating: 4.9,
    img: "https://images.unsplash.com/photo-1616455579100-2ceaa4eb2d37?auto=format&fit=crop&w=800&q=80",
    desc: "Modern classic roadster, liquid-cooled TR-Series engine, 37.5 Nm torque, switchable traction control."
  },
  {
    id: "re-hunter-350",
    name: "Royal Enfield Hunter 350",
    brand: "Royal Enfield",
    category: "Retro",
    year: 2024,
    priceInr: 149900,
    priceDisplay: "₹1,49,900 ($1,800)",
    powerHp: 20.2,
    engineCc: 349,
    mileage: 36.2,
    weightKg: 181,
    topSpeed: 114,
    seatHeightMm: 790,
    rating: 4.5,
    img: "https://images.unsplash.com/photo-1599819811279-d5ad9cccf838?auto=format&fit=crop&w=800&q=80",
    desc: "J-Series refined engine, compact wheelbase, agile city handling, relaxed urban ergonomics."
  },
  {
    id: "ktm-390-adventure",
    name: "KTM 390 Adventure",
    brand: "KTM",
    category: "Adventure",
    year: 2024,
    priceInr: 338000,
    priceDisplay: "₹3,38,000 ($4,100)",
    powerHp: 43.5,
    engineCc: 373,
    mileage: 28.0,
    weightKg: 177,
    topSpeed: 165,
    seatHeightMm: 855,
    rating: 4.8,
    img: "https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=800&q=80",
    desc: "Cornering ABS, lean-sensitive traction control, off-road riding mode, long travel WP Apex suspension."
  },
  {
    id: "bmw-s1000rr",
    name: "BMW S 1000 RR",
    brand: "BMW",
    category: "Super Sport",
    year: 2024,
    priceInr: 2075000,
    priceDisplay: "₹20,75,000 ($18,295)",
    powerHp: 205.0,
    engineCc: 999,
    mileage: 15.0,
    weightKg: 197,
    topSpeed: 303,
    seatHeightMm: 824,
    rating: 5.0,
    img: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
    desc: "ShiftCam variable intake timing, carbon aero winglets, Dynamic Traction Control, Race ABS Pro."
  },
  {
    id: "ducati-panigale-v4",
    name: "Ducati Panigale V4",
    brand: "Ducati",
    category: "Super Sport",
    year: 2024,
    priceInr: 2772000,
    priceDisplay: "₹27,72,000 ($24,995)",
    powerHp: 215.5,
    engineCc: 1103,
    mileage: 13.5,
    weightKg: 195,
    topSpeed: 315,
    seatHeightMm: 835,
    rating: 4.9,
    img: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
    desc: "Desmosedici Stradale 90° V4, counter-rotating crankshaft, Ohlins electronic suspension, Brembo Stylema."
  },
  {
    id: "honda-cb350rs",
    name: "Honda CB350RS",
    brand: "Honda",
    category: "Retro",
    year: 2024,
    priceInr: 214000,
    priceDisplay: "₹2,14,000 ($2,600)",
    powerHp: 20.8,
    engineCc: 348,
    mileage: 35.0,
    weightKg: 179,
    topSpeed: 125,
    seatHeightMm: 800,
    rating: 4.7,
    img: "https://images.unsplash.com/photo-1591637333184-19aa84b3e01f?auto=format&fit=crop&w=800&q=80",
    desc: "Refined long-stroke counterbalanced thumper, Honda Selectable Torque Control (HSTC), assist slipper clutch."
  }
];

// App State
let selectedCategory = "All";
let searchQuery = "";
let compareList = [];

// Initialize
document.addEventListener("DOMContentLoaded", () => {
  renderBikes();
  updateEmi();
});

// Render Bikes
function renderBikes() {
  const grid = document.getElementById("bikesGrid");
  const sort = document.getElementById("sortSelect").value;

  let filtered = MOTORCYCLES.filter(b => {
    const matchCat = selectedCategory === "All" || b.category.toLowerCase().includes(selectedCategory.toLowerCase());
    const matchQuery = !searchQuery || b.name.toLowerCase().includes(searchQuery.toLowerCase()) || b.brand.toLowerCase().includes(searchQuery.toLowerCase());
    return matchCat && matchQuery;
  });

  // Sorting
  if (sort === "price_low") filtered.sort((a,b) => a.priceInr - b.priceInr);
  else if (sort === "price_high") filtered.sort((a,b) => b.priceInr - a.priceInr);
  else if (sort === "power") filtered.sort((a,b) => b.powerHp - a.powerHp);
  else if (sort === "displacement") filtered.sort((a,b) => b.engineCc - a.engineCc);

  document.getElementById("catalogCount").innerText = `Showing ${filtered.length} machines`;

  if (filtered.length === 0) {
    grid.innerHTML = `
      <div class="col-span-full py-16 text-center text-slate-400">
        <i class="fa-solid fa-motorcycle text-5xl text-slate-700 mb-3 block"></i>
        <p class="text-lg font-bold text-white">No bikes found</p>
        <p class="text-xs mt-1">Try another search or ask our Moto AI Advisor!</p>
        <button onclick="openAiAdvisorModal()" class="mt-4 bg-cyanNeon text-slate950 font-bold px-4 py-2 rounded-xl text-xs">Ask Moto AI Advisor</button>
      </div>
    `;
    return;
  }

  grid.innerHTML = filtered.map(b => {
    const isComparing = compareList.includes(b.id);
    return `
      <div class="bg-slate900 border border-slate800 rounded-2xl overflow-hidden hover:border-slate700 transition flex flex-col group shadow-lg">
        <!-- Image Banner -->
        <div class="relative h-48 bg-slate950 overflow-hidden cursor-pointer" onclick="openDetailModal('${b.id}')">
          <img src="${b.img}" alt="${b.name}" class="w-full h-full object-cover group-hover:scale-105 transition duration-500">
          <div class="absolute inset-0 bg-gradient-to-t from-slate900 via-transparent to-transparent"></div>
          <span class="absolute top-3 left-3 bg-slate950/80 backdrop-blur-sm border border-slate700 text-cyanNeon text-[11px] font-bold px-2.5 py-1 rounded-lg">
            ${b.brand.toUpperCase()}
          </span>
          <span class="absolute top-3 right-3 bg-slate950/80 backdrop-blur-sm border border-slate700 text-amber-400 text-xs font-bold px-2 py-0.5 rounded-lg flex items-center gap-1">
            <i class="fa-solid fa-star text-[10px]"></i> ${b.rating}
          </span>
        </div>

        <!-- Body -->
        <div class="p-5 flex-grow flex flex-col justify-between">
          <div>
            <div class="flex items-start justify-between gap-2">
              <h3 class="font-bold text-white text-lg group-hover:text-cyanNeon transition">${b.name}</h3>
              <span class="text-[11px] font-semibold text-slate-400 bg-slate800 px-2 py-0.5 rounded">${b.category}</span>
            </div>
            <div class="text-xs text-trackGreen font-bold mt-1">${b.priceDisplay}</div>

            <!-- Spec Badges Grid -->
            <div class="grid grid-cols-3 gap-2 mt-4 text-center">
              <div class="bg-slate950 border border-slate800 rounded-xl p-2">
                <span class="text-[10px] text-slate-500 block uppercase">Power</span>
                <span class="text-xs font-bold text-white">${b.powerHp} HP</span>
              </div>
              <div class="bg-slate950 border border-slate800 rounded-xl p-2">
                <span class="text-[10px] text-slate-500 block uppercase">Engine</span>
                <span class="text-xs font-bold text-white">${b.engineCc} cc</span>
              </div>
              <div class="bg-slate950 border border-slate800 rounded-xl p-2">
                <span class="text-[10px] text-slate-500 block uppercase">Mileage</span>
                <span class="text-xs font-bold text-cyanNeon">${b.mileage} km/l</span>
              </div>
            </div>
          </div>

          <!-- Bottom Actions -->
          <div class="flex items-center gap-2 mt-5 pt-4 border-t border-slate800">
            <button onclick="toggleCompare('${b.id}')" class="flex-1 py-2 rounded-xl text-xs font-bold transition flex items-center justify-center gap-1.5 ${isComparing ? 'bg-cyanNeon text-slate950' : 'bg-slate800 hover:bg-slate700 text-white'}">
              <i class="fa-solid ${isComparing ? 'fa-check' : 'fa-code-compare'}"></i>
              ${isComparing ? 'Comparing' : 'Compare'}
            </button>
            <button onclick="openDetailModal('${b.id}')" class="px-3.5 py-2 bg-slate950 hover:bg-slate800 border border-slate700 text-slate-300 rounded-xl text-xs font-semibold transition" title="View Full Specs">
              Specs
            </button>
          </div>
        </div>
      </div>
    `;
  }).join("");
}

// Category filter
function setCategory(cat) {
  selectedCategory = cat;
  document.querySelectorAll(".cat-chip").forEach(btn => {
    btn.classList.remove("active-cat", "bg-cyanNeon", "text-slate950", "font-bold");
    btn.classList.add("bg-slate900", "border-slate800", "text-slate-300");
  });
  event.target.classList.add("active-cat", "bg-cyanNeon", "text-slate950", "font-bold");
  event.target.classList.remove("bg-slate900", "border-slate800", "text-slate-300");
  renderBikes();
}

// Search
document.getElementById("searchInput").addEventListener("input", (e) => {
  searchQuery = e.target.value.trim();
  renderBikes();
});
function clearOrSearch() {
  renderBikes();
}

// Compare Drawer Logic
function toggleCompare(id) {
  if (compareList.includes(id)) {
    compareList = compareList.filter(item => item !== id);
  } else {
    if (compareList.length >= 3) {
      alert("You can compare up to 3 motorcycles side-by-side.");
      return;
    }
    compareList.push(id);
  }
  updateCompareDock();
  renderBikes();
}

function clearCompare() {
  compareList = [];
  updateCompareDock();
  renderBikes();
}

function updateCompareDock() {
  const dock = document.getElementById("compareDock");
  const thumbs = document.getElementById("dockThumbnails");
  const badge = document.getElementById("navCompareBadge");

  badge.innerText = compareList.length;
  if (compareList.length > 0) {
    badge.classList.remove("hidden");
    dock.classList.remove("hidden");
    dock.classList.add("flex");

    thumbs.innerHTML = compareList.map(id => {
      const b = MOTORCYCLES.find(m => m.id === id);
      return `
        <div class="relative w-8 h-8 rounded-lg overflow-hidden border border-cyanNeon">
          <img src="${b.img}" alt="${b.name}" class="w-full h-full object-cover">
        </div>
      `;
    }).join("");
  } else {
    badge.classList.add("hidden");
    dock.classList.add("hidden");
    dock.classList.remove("flex");
  }
}

// Modals
function openDetailModal(id) {
  const b = MOTORCYCLES.find(m => m.id === id);
  const container = document.getElementById("detailModalContent");
  container.innerHTML = `
    <div class="relative h-64 rounded-xl overflow-hidden mb-4 bg-slate950">
      <img src="${b.img}" class="w-full h-full object-cover">
      <span class="absolute top-3 left-3 bg-slate950/80 text-cyanNeon text-xs font-bold px-3 py-1 rounded-md border border-slate700">
        ${b.brand} • ${b.year}
      </span>
    </div>
    <div class="flex justify-between items-start gap-4">
      <div>
        <h2 class="text-2xl font-black text-white">${b.name}</h2>
        <span class="text-xs text-slate-400">${b.category} Category</span>
      </div>
      <div class="text-right">
        <div class="text-xl font-bold text-trackGreen">${b.priceDisplay}</div>
        <div class="text-xs text-slate-400">Ex-showroom MSRP</div>
      </div>
    </div>
    <p class="text-slate-300 text-sm mt-3 leading-relaxed">${b.desc}</p>

    <!-- Spec Table -->
    <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 mt-5">
      <div class="bg-slate950 p-3 rounded-xl border border-slate800">
        <span class="text-[11px] text-slate-500 block">Peak Power</span>
        <span class="text-sm font-bold text-white">${b.powerHp} HP</span>
      </div>
      <div class="bg-slate950 p-3 rounded-xl border border-slate800">
        <span class="text-[11px] text-slate-500 block">Displacement</span>
        <span class="text-sm font-bold text-white">${b.engineCc} cc</span>
      </div>
      <div class="bg-slate950 p-3 rounded-xl border border-slate800">
        <span class="text-[11px] text-slate-500 block">Fuel Mileage</span>
        <span class="text-sm font-bold text-cyanNeon">${b.mileage} kmpl</span>
      </div>
      <div class="bg-slate950 p-3 rounded-xl border border-slate800">
        <span class="text-[11px] text-slate-500 block">Kerb Weight</span>
        <span class="text-sm font-bold text-white">${b.weightKg} kg</span>
      </div>
      <div class="bg-slate950 p-3 rounded-xl border border-slate800">
        <span class="text-[11px] text-slate-500 block">Top Speed</span>
        <span class="text-sm font-bold text-white">${b.topSpeed} km/h</span>
      </div>
      <div class="bg-slate950 p-3 rounded-xl border border-slate800">
        <span class="text-[11px] text-slate-500 block">Seat Height</span>
        <span class="text-sm font-bold text-white">${b.seatHeightMm} mm</span>
      </div>
    </div>

    <!-- Actions -->
    <div class="flex items-center gap-3 mt-6 pt-4 border-t border-slate800">
      <button onclick="toggleCompare('${b.id}'); closeDetailModal();" class="flex-1 bg-cyanNeon text-slate950 font-bold py-2.5 rounded-xl text-xs hover:bg-cyan-300">
        ${compareList.includes(b.id) ? 'Remove from Compare' : 'Add to Comparison'}
      </button>
      <button onclick="closeDetailModal(); openEmiModalForPrice(${b.priceInr});" class="flex-1 bg-slate800 text-white font-bold py-2.5 rounded-xl text-xs hover:bg-slate700">
        Calculate EMI
      </button>
    </div>
  `;
  document.getElementById("detailModal").classList.remove("hidden");
}

function closeDetailModal() {
  document.getElementById("detailModal").classList.add("hidden");
}

// Compare Modal
function openCompareModal() {
  if (compareList.length < 2) {
    alert("Please select at least 2 motorcycles to compare. Tap 'Compare' on bike cards.");
    return;
  }
  const bikes = compareList.map(id => MOTORCYCLES.find(m => m.id === id));
  const container = document.getElementById("compareModalContent");

  container.innerHTML = `
    <!-- Top Row Bikes -->
    <div class="grid grid-cols-${bikes.length} gap-4 mb-6">
      ${bikes.map(b => `
        <div class="bg-slate950 border border-slate800 rounded-xl p-3 text-center">
          <img src="${b.img}" class="h-28 w-full object-cover rounded-lg mb-2">
          <h4 class="font-bold text-white text-sm">${b.name}</h4>
          <span class="text-xs text-trackGreen font-bold">${b.priceDisplay}</span>
        </div>
      `).join("")}
    </div>

    <!-- Shootout Table -->
    <div class="overflow-x-auto">
      <table class="w-full text-left text-xs border border-slate800">
        <thead class="bg-slate950 text-slate-400 uppercase text-[10px]">
          <tr>
            <th class="p-3 border-b border-slate800">Specification</th>
            ${bikes.map(b => `<th class="p-3 border-b border-slate800">${b.name}</th>`).join("")}
          </tr>
        </thead>
        <tbody class="divide-y divide-slate800">
          <tr><td class="p-3 text-slate-400 font-semibold">Engine</td>${bikes.map(b => `<td class="p-3 font-bold text-white">${b.engineCc} cc</td>`).join("")}</tr>
          <tr><td class="p-3 text-slate-400 font-semibold">Max Power</td>${bikes.map(b => `<td class="p-3 font-bold text-white">${b.powerHp} HP</td>`).join("")}</tr>
          <tr><td class="p-3 text-slate-400 font-semibold">Fuel Mileage</td>${bikes.map(b => `<td class="p-3 font-bold text-cyanNeon">${b.mileage} km/l</td>`).join("")}</tr>
          <tr><td class="p-3 text-slate-400 font-semibold">Kerb Weight</td>${bikes.map(b => `<td class="p-3 font-bold text-white">${b.weightKg} kg</td>`).join("")}</tr>
          <tr><td class="p-3 text-slate-400 font-semibold">Top Speed</td>${bikes.map(b => `<td class="p-3 font-bold text-white">${b.topSpeed} km/h</td>`).join("")}</tr>
          <tr><td class="p-3 text-slate-400 font-semibold">Seat Height</td>${bikes.map(b => `<td class="p-3 font-bold text-white">${b.seatHeightMm} mm</td>`).join("")}</tr>
        </tbody>
      </table>
    </div>

    <!-- Gemini AI Shootout Verdict Button -->
    <div class="mt-6 p-4 bg-slate950 border border-cyanNeon/40 rounded-xl">
      <div class="flex items-center justify-between mb-2">
        <div class="flex items-center gap-2">
          <i class="fa-solid fa-wand-magic-sparkles text-cyanNeon text-sm"></i>
          <span class="text-xs font-bold text-cyanNeon uppercase tracking-wider">Gemini 2.5 AI Shootout Verdict</span>
        </div>
        <button onclick="getAiCompareVerdict()" id="aiVerdictBtn" class="bg-cyanNeon text-slate950 font-bold px-3 py-1 rounded-lg text-xs hover:bg-cyan-300">
          Generate Verdict
        </button>
      </div>
      <div id="aiVerdictBox" class="text-xs text-slate-300 leading-relaxed">
        Tap 'Generate Verdict' to have Google Gemini synthesize a head-to-head decision based on power-to-weight, ergonomics, and value.
      </div>
    </div>
  `;
  document.getElementById("compareModal").classList.remove("hidden");
}

function closeCompareModal() {
  document.getElementById("compareModal").classList.add("hidden");
}

// EMI Modal Logic
function openEmiModal() {
  document.getElementById("emiModal").classList.remove("hidden");
}
function openEmiModalForPrice(price) {
  document.getElementById("emiPriceSlider").value = price;
  document.getElementById("emiDownSlider").value = Math.round(price * 0.2);
  updateEmi();
  openEmiModal();
}
function closeEmiModal() {
  document.getElementById("emiModal").classList.add("hidden");
}
function updateEmi() {
  const price = parseFloat(document.getElementById("emiPriceSlider").value);
  const down = Math.min(price, parseFloat(document.getElementById("emiDownSlider").value));
  const rate = parseFloat(document.getElementById("emiRateSlider").value);
  const months = parseInt(document.getElementById("emiTenureSlider").value);

  document.getElementById("emiPriceVal").innerText = "₹" + price.toLocaleString("en-IN");
  document.getElementById("emiDownVal").innerText = "₹" + down.toLocaleString("en-IN");
  document.getElementById("emiRateVal").innerText = rate + "%";
  document.getElementById("emiTenureVal").innerText = months + " Mos";

  const loan = Math.max(0, price - down);
  const r = (rate / 12) / 100;
  let emi = 0;
  if (r > 0 && months > 0 && loan > 0) {
    emi = Math.round((loan * r * Math.pow(1 + r, months)) / (Math.pow(1 + r, months) - 1));
  } else if (months > 0) {
    emi = Math.round(loan / months);
  }

  const totalPayable = emi * months;
  const totalInterest = Math.max(0, totalPayable - loan);

  document.getElementById("emiMonthlyResult").innerText = "₹" + emi.toLocaleString("en-IN") + " / mo";
  document.getElementById("emiLoanResult").innerText = "₹" + loan.toLocaleString("en-IN");
  document.getElementById("emiInterestResult").innerText = "₹" + totalInterest.toLocaleString("en-IN");
}

// AI Advisor Modal & Chat
function openAiAdvisorModal() {
  document.getElementById("aiModal").classList.remove("hidden");
  document.getElementById("aiInput").focus();
}
function closeAiAdvisorModal() {
  document.getElementById("aiModal").classList.add("hidden");
}

function sendQuickPrompt(text) {
  document.getElementById("aiInput").value = text;
  sendAiMessage();
}

async function sendAiMessage() {
  const input = document.getElementById("aiInput");
  const query = input.value.trim();
  if (!query) return;

  const chatBox = document.getElementById("aiChatBox");

  // User bubble
  chatBox.innerHTML += `
    <div class="flex gap-2.5 items-start justify-end">
      <div class="bg-cyanNeon text-slate950 text-xs md:text-sm font-semibold p-3 rounded-2xl rounded-tr-none max-w-[80%]">
        ${query}
      </div>
    </div>
  `;
  input.value = "";
  chatBox.scrollTop = chatBox.scrollHeight;

  // Loading indicator
  const loadId = "load_" + Date.now();
  chatBox.innerHTML += `
    <div id="${loadId}" class="flex gap-2.5 items-start">
      <div class="w-7 h-7 rounded-full bg-cyanNeon/20 text-cyanNeon flex items-center justify-center text-xs shrink-0 mt-0.5">
        <i class="fa-solid fa-robot"></i>
      </div>
      <div class="bg-slate850 text-slate-400 text-xs p-3 rounded-2xl rounded-tl-none flex items-center gap-2">
        <span class="w-2 h-2 rounded-full bg-cyanNeon animate-ping"></span>
        Analyzing engineering specs...
      </div>
    </div>
  `;
  chatBox.scrollTop = chatBox.scrollHeight;

  try {
    const catalogSummary = MOTORCYCLES.map(b => `- ${b.name} (${b.category}, ${b.engineCc}cc, ${b.powerHp}HP, ${b.priceDisplay}, ${b.mileage}kmpl)`).join("\n");
    const systemPrompt = `You are MotoScope AI, a world-class motorcycle engineer & buyer consultant. App catalog:\n${catalogSummary}\nAnswer concisely with bullet points.`;

    const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${getGeminiKey()}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ role: "user", parts: [{ text: `${systemPrompt}\n\nUser Question:\n${query}` }] }]
      })
    });
    const data = await res.json();
    const reply = data.candidates?.[0]?.content?.parts?.[0]?.text || "No response received.";

    document.getElementById(loadId).remove();
    chatBox.innerHTML += `
      <div class="flex gap-2.5 items-start">
        <div class="w-7 h-7 rounded-full bg-cyanNeon/20 text-cyanNeon flex items-center justify-center text-xs shrink-0 mt-0.5">
          <i class="fa-solid fa-robot"></i>
        </div>
        <div class="bg-slate850 border border-slate800 text-slate-200 text-xs md:text-sm p-3.5 rounded-2xl rounded-tl-none max-w-[85%] leading-relaxed whitespace-pre-line">
          ${reply}
        </div>
      </div>
    `;
  } catch (err) {
    document.getElementById(loadId).remove();
    chatBox.innerHTML += `
      <div class="flex gap-2.5 items-start">
        <div class="w-7 h-7 rounded-full bg-red-500/20 text-red-400 flex items-center justify-center text-xs shrink-0 mt-0.5">
          <i class="fa-solid fa-triangle-exclamation"></i>
        </div>
        <div class="bg-slate850 border border-red-500/30 text-red-300 text-xs p-3 rounded-2xl">
          Could not reach Gemini AI: ${err.message}. Please check your internet connection.
        </div>
      </div>
    `;
  }
  chatBox.scrollTop = chatBox.scrollHeight;
}

async function getAiCompareVerdict() {
  const btn = document.getElementById("aiVerdictBtn");
  const box = document.getElementById("aiVerdictBox");
  const bikes = compareList.map(id => MOTORCYCLES.find(m => m.id === id));

  btn.disabled = true;
  btn.innerText = "Analyzing...";
  box.innerHTML = `<span class="inline-flex items-center gap-2 text-cyanNeon"><span class="w-2 h-2 rounded-full bg-cyanNeon animate-ping"></span> Gemini AI is analyzing dyno curves and ergonomics...</span>`;

  try {
    const summary = bikes.map(b => `${b.name}: ${b.engineCc}cc, ${b.powerHp}HP, ${b.mileage}kmpl, ${b.weightKg}kg, ${b.priceDisplay}`).join(" vs ");
    const prompt = `Compare these motorcycles head-to-head for a buyer: ${summary}. Give: 1. Winner Verdict, 2. Who should buy which, 3. Key trade-offs. Keep it punchy with bullet points.`;

    const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${getGeminiKey()}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ role: "user", parts: [{ text: prompt }] }]
      })
    });
    const data = await res.json();
    const reply = data.candidates?.[0]?.content?.parts?.[0]?.text || "Verdict unavailable.";
    box.innerHTML = `<div class="whitespace-pre-line text-white">${reply}</div>`;
  } catch (err) {
    box.innerText = "Error fetching AI verdict: " + err.message;
  }
  btn.disabled = false;
  btn.innerText = "Regenerate";
}
