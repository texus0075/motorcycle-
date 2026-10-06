// MotoScope Web Application Core Logic (1:1 with Android App UI)
const getGeminiKey = () => atob("QVEuQWI4Uk42S1E3clhXT3JBSHBpaXlWVFoydzBRWjRSRmNwanZVanNNQXJ5akw1UHpZeUE=");

// Default Master Motorcycle Dataset
const DEFAULT_MOTORCYCLES = [
  {
    id: "yamaha-r15-v4",
    name: "YZF-R15 V4",
    brand: "Yamaha",
    category: "Sport",
    year: 2024,
    priceInr: 182000,
    priceDisplay: "$2,200 (approx. ₹1,82,000)",
    powerHp: 18.4,
    engineCc: 155,
    mileage: 46.0,
    weightKg: 142,
    topSpeed: 142,
    rating: 4.8,
    img: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
    source: "Yamaha Motor Global Technical Homologation Sheet",
    desc: "Liquid-cooled 4-valve VVA, Deltabox steel frame, Assist & Slipper clutch, Dual-Channel ABS."
  },
  {
    id: "bmw-s1000rr",
    name: "S 1000 RR",
    brand: "BMW",
    category: "Super Sport",
    year: 2024,
    priceInr: 2075000,
    priceDisplay: "$18,295 (approx. ₹20,75,000)",
    powerHp: 205.0,
    engineCc: 999,
    mileage: 15.6,
    weightKg: 197,
    topSpeed: 303,
    rating: 4.9,
    img: "https://images.unsplash.com/photo-1591637333184-19aa84b3e01f?auto=format&fit=crop&w=800&q=80",
    source: "BMW Motorrad AG Homologation Datasheet",
    desc: "ShiftCam variable valve timing, carbon aerodynamic winglets, 6-axis IMU DTC, Launch Control."
  },
  {
    id: "triumph-street-triple-765",
    name: "Street Triple 765 RS",
    brand: "Triumph",
    category: "Naked",
    year: 2024,
    priceInr: 1207000,
    priceDisplay: "$12,595 (approx. ₹12,07,000)",
    powerHp: 130.0,
    engineCc: 765,
    mileage: 19.2,
    weightKg: 188,
    topSpeed: 245,
    rating: 4.9,
    img: "https://images.unsplash.com/photo-1558981420-87aa9dad1c89?auto=format&fit=crop&w=800&q=80",
    source: "Triumph Motorcycles Official Specification",
    desc: "Moto2 derived inline-3 screamer, Brembo Stylema calipers, Ohlins STX40 rear shock, track-tuned quickshifter."
  },
  {
    id: "ducati-panigale-v4",
    name: "Panigale V4 S",
    brand: "Ducati",
    category: "Super Sport",
    year: 2024,
    priceInr: 2790000,
    priceDisplay: "$31,995 (approx. ₹27,90,000)",
    powerHp: 215.5,
    engineCc: 1103,
    mileage: 13.1,
    weightKg: 195,
    topSpeed: 315,
    rating: 4.9,
    img: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
    source: "Ducati Motor Holding S.p.A. Datasheet",
    desc: "Desmosedici Stradale 90° V4 counter-rotating crankshaft, Ohlins Smart EC 2.0 electronic suspension."
  },
  {
    id: "ktm-rc-200",
    name: "RC 200",
    brand: "KTM",
    category: "Super Sport",
    year: 2024,
    priceInr: 218000,
    priceDisplay: "$2,650 (approx. ₹2,18,000)",
    powerHp: 25.0,
    engineCc: 199,
    mileage: 35.0,
    weightKg: 151,
    topSpeed: 145,
    rating: 4.6,
    img: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
    source: "KTM Sportmotorcycle GmbH Datasheet",
    desc: "Lightweight trellis frame, WP APEX inverted suspension, ByBre 320mm front disc, Supermoto ABS."
  },
  {
    id: "kawasaki-ninja-300",
    name: "Ninja 300",
    brand: "Kawasaki",
    category: "Sport",
    year: 2024,
    priceInr: 343000,
    priceDisplay: "$4,150 (approx. ₹3,43,000)",
    powerHp: 39.0,
    engineCc: 296,
    mileage: 30.0,
    weightKg: 179,
    topSpeed: 175,
    rating: 4.7,
    img: "https://images.unsplash.com/photo-1591637333184-19aa84b3e01f?auto=format&fit=crop&w=800&q=80",
    source: "Kawasaki Heavy Industries Specification",
    desc: "Parallel-twin 8-valve DOHC screamer, race-derived slipper clutch, manageable 785mm seat height."
  },
  {
    id: "triumph-speed-400",
    name: "Speed 400",
    brand: "Triumph",
    category: "Naked",
    year: 2024,
    priceInr: 233000,
    priceDisplay: "$2,850 (approx. ₹2,33,000)",
    powerHp: 40.0,
    engineCc: 398,
    mileage: 29.0,
    weightKg: 170,
    topSpeed: 158,
    rating: 4.9,
    img: "https://images.unsplash.com/photo-1616455579100-2ceaa4eb2d37?auto=format&fit=crop&w=800&q=80",
    source: "Triumph Motorcycles Official Specification",
    desc: "Modern classic roadster, liquid-cooled TR-Series engine, 37.5 Nm torque, switchable traction control."
  },
  {
    id: "re-hunter-350",
    name: "Hunter 350",
    brand: "Royal Enfield",
    category: "Retro",
    year: 2024,
    priceInr: 149900,
    priceDisplay: "$1,800 (approx. ₹1,49,900)",
    powerHp: 20.2,
    engineCc: 349,
    mileage: 36.2,
    weightKg: 181,
    topSpeed: 114,
    rating: 4.5,
    img: "https://images.unsplash.com/photo-1599819811279-d5ad9cccf838?auto=format&fit=crop&w=800&q=80",
    source: "Royal Enfield Technical Specification",
    desc: "J-Series refined engine, compact wheelbase, agile city handling, relaxed urban ergonomics."
  },
  {
    id: "ktm-390-adventure",
    name: "390 Adventure",
    brand: "KTM",
    category: "Adventure",
    year: 2024,
    priceInr: 338000,
    priceDisplay: "$4,100 (approx. ₹3,38,000)",
    powerHp: 43.5,
    engineCc: 373,
    mileage: 28.0,
    weightKg: 177,
    topSpeed: 160,
    rating: 4.8,
    img: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
    source: "KTM Sportmotorcycle GmbH Datasheet",
    desc: "Dakar rally-bred geometry, cornering ABS, traction control, off-road ride mode, WP APEX long travel suspension."
  },
  {
    id: "honda-cbr650r",
    name: "CBR650R",
    brand: "Honda",
    category: "Super Sport",
    year: 2024,
    priceInr: 935000,
    priceDisplay: "$9,800 (approx. ₹9,35,000)",
    powerHp: 87.0,
    engineCc: 649,
    mileage: 20.4,
    weightKg: 211,
    topSpeed: 220,
    rating: 4.8,
    img: "https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=800&q=80",
    source: "Honda Motor Co. Technical Datasheet",
    desc: "Silky 16-valve inline-four, Showa SFF-BP inverted forks, Honda Selectable Torque Control (HSTC)."
  }
];

// Brands Dataset
const BRANDS = [
  { id: "yamaha", name: "Yamaha", country: "Japan", year: 1955, color: "#0038A8", icon: "fa-motorcycle" },
  { id: "ktm", name: "KTM", country: "Austria", year: 1934, color: "#FF6600", icon: "fa-bolt" },
  { id: "kawasaki", name: "Kawasaki", country: "Japan", year: 1896, color: "#49C300", icon: "fa-flag-checkered" },
  { id: "ducati", name: "Ducati", country: "Italy", year: 1926, color: "#CC0000", icon: "fa-fire" },
  { id: "bmw", name: "BMW Motorrad", country: "Germany", year: 1923, color: "#0066B1", icon: "fa-shield-halved" },
  { id: "honda", name: "Honda", country: "Japan", year: 1948, color: "#E4002B", icon: "fa-trophy" },
  { id: "triumph", name: "Triumph", country: "UK", year: 1902, color: "#0B1F3F", icon: "fa-crown" },
  { id: "royalenfield", name: "Royal Enfield", country: "India / UK", year: 1901, color: "#D4AF37", icon: "fa-compass" },
  { id: "aprilia", name: "Aprilia", country: "Italy", year: 1945, color: "#D62226", icon: "fa-gauge-high" },
  { id: "suzuki", name: "Suzuki", country: "Japan", year: 1909, color: "#005BAA", icon: "fa-wind" }
];

// Application State
let motorcycles = [];
let compareSlots = [];
let savedBikes = [];
let currentCategory = "All";
let currentSearch = "";
let currentTab = "home";

// Initialize Data from LocalStorage
function initData() {
  const storedBikes = localStorage.getItem("motoscope_bikes");
  if (storedBikes) {
    try { motorcycles = JSON.parse(storedBikes); } catch (e) { motorcycles = [...DEFAULT_MOTORCYCLES]; }
  } else {
    motorcycles = [...DEFAULT_MOTORCYCLES];
    localStorage.setItem("motoscope_bikes", JSON.stringify(motorcycles));
  }

  const storedSlots = localStorage.getItem("motoscope_compare");
  if (storedSlots) {
    try { compareSlots = JSON.parse(storedSlots); } catch (e) { compareSlots = []; }
  }

  const storedSaved = localStorage.getItem("motoscope_saved");
  if (storedSaved) {
    try { savedBikes = JSON.parse(storedSaved); } catch (e) { savedBikes = []; }
  }
}

// Tab Switching Logic
function switchTab(tab) {
  currentTab = tab;
  const tabs = ["home", "explore", "brands", "compare", "saved", "studio"];
  tabs.forEach(t => {
    const el = document.getElementById(`tab-${t}`);
    const navBtn = document.getElementById(`nav-btn-${t}`);
    if (el) el.classList.toggle("hidden", t !== tab);
    if (navBtn) {
      if (t === tab) {
        navBtn.classList.add("text-cyanNeon");
        navBtn.classList.remove("text-slate-400");
      } else {
        navBtn.classList.remove("text-cyanNeon");
        navBtn.classList.add("text-slate-400");
      }
    }
  });

  window.scrollTo({ top: 0, behavior: "smooth" });
  renderCurrentTab();
  updateComparisonSlotBar();
}

// Render Master Card (Exact 1:1 Match with Android MotorcycleCard.kt)
function renderBikeCard(bike) {
  const isInCompare = compareSlots.includes(bike.id);
  const isFav = savedBikes.includes(bike.id);

  return `
    <div class="motorcycle-card bg-slate850 border ${isInCompare ? 'border-cyanNeon shadow-lg shadow-cyan-500/10' : 'border-slate800'} rounded-2xl overflow-hidden hover:border-slate700 transition">
      <!-- Image with overlay badges -->
      <div class="relative h-44 sm:h-52 bg-slate950 overflow-hidden">
        <img src="${bike.img}" alt="${bike.name}" class="w-full h-full object-cover">
        
        <!-- Category Badge Top-Left -->
        <span class="absolute top-2.5 left-2.5 bg-slate950/80 backdrop-blur-md border border-cyan-500/40 text-cyanNeon text-[10px] font-black uppercase px-2.5 py-0.5 rounded-lg tracking-wider">
          ${bike.category}
        </span>

        <!-- Bookmark Button Top-Right -->
        <button onclick="toggleFavorite('${bike.id}')" title="Save offline" class="absolute top-2.5 right-2.5 w-8 h-8 rounded-full bg-slate950/80 backdrop-blur-md border border-slate700 ${isFav ? 'text-amberOrange' : 'text-slate-400 hover:text-white'} flex items-center justify-center transition">
          <i class="${isFav ? 'fa-solid' : 'fa-regular'} fa-bookmark text-sm"></i>
        </button>

        <!-- Brand Badge Bottom-Left -->
        <span class="absolute bottom-2.5 left-2.5 bg-slate950/90 text-white font-black text-[11px] tracking-wider px-2 py-0.5 rounded uppercase">
          ${bike.brand}
        </span>
      </div>

      <!-- Card Body -->
      <div class="p-4 space-y-3">
        <div class="flex items-center justify-between">
          <h3 class="text-base font-bold text-white tracking-wide">${bike.name}</h3>
          <span class="text-amber-400 text-xs font-bold flex items-center gap-1">
            ★ ${bike.rating || '4.8'}
          </span>
        </div>

        <!-- 3 Spec Boxes (Power, Engine, Weight) -->
        <div class="grid grid-cols-3 gap-2 text-center text-xs">
          <div class="bg-slate900 border border-slate800/80 p-2 rounded-xl">
            <span class="text-[9px] uppercase tracking-wider text-slate-500 block font-bold">POWER</span>
            <span class="text-slate-100 font-bold">${bike.powerHp} HP</span>
          </div>
          <div class="bg-slate900 border border-slate800/80 p-2 rounded-xl">
            <span class="text-[9px] uppercase tracking-wider text-slate-500 block font-bold">ENGINE</span>
            <span class="text-slate-100 font-bold">${bike.engineCc} cc</span>
          </div>
          <div class="bg-slate900 border border-slate800/80 p-2 rounded-xl">
            <span class="text-[9px] uppercase tracking-wider text-slate-500 block font-bold">WEIGHT</span>
            <span class="text-slate-100 font-bold">${bike.weightKg} kg</span>
          </div>
        </div>

        <!-- Price & Compare Button -->
        <div class="flex items-center justify-between pt-1">
          <div>
            <span class="text-[9px] uppercase font-bold text-slate-500 block">EST. PRICE</span>
            <span class="text-trackGreen font-extrabold text-xs sm:text-sm">${bike.priceDisplay}</span>
          </div>
          <button onclick="toggleCompare('${bike.id}')" class="px-3 py-1.5 rounded-xl text-xs font-bold transition flex items-center gap-1.5 ${isInCompare ? 'bg-cyanNeon text-slate950' : 'bg-slate900 border border-cyan-500/40 text-cyanNeon hover:bg-cyan-500/10'}">
            <i class="fa-solid fa-arrow-right-arrow-left text-[11px]"></i>
            <span>${isInCompare ? 'In Compare' : 'Compare'}</span>
          </button>
        </div>
      </div>
    </div>
  `;
}

// Render Current Tab
function renderCurrentTab() {
  if (currentTab === "home") renderHomeFeed();
  else if (currentTab === "explore") renderExploreGrid();
  else if (currentTab === "brands") renderBrandsGrid();
  else if (currentTab === "compare") renderCompareView();
  else if (currentTab === "saved") renderSavedFeed();
  else if (currentTab === "studio") renderStudioView();
}

// 1. Home Feed
function renderHomeFeed() {
  const feed = document.getElementById("homeBikeFeed");
  if (!feed) return;

  const filtered = motorcycles.filter(b => {
    const matchCat = currentCategory === "All" || b.category.toLowerCase() === currentCategory.toLowerCase();
    const matchSearch = currentSearch === "" || 
      b.name.toLowerCase().includes(currentSearch.toLowerCase()) || 
      b.brand.toLowerCase().includes(currentSearch.toLowerCase()) ||
      b.category.toLowerCase().includes(currentSearch.toLowerCase());
    return matchCat && matchSearch;
  });

  if (filtered.length === 0) {
    feed.innerHTML = `
      <div class="text-center py-12 text-slate-500 space-y-2">
        <i class="fa-solid fa-motorcycle text-3xl"></i>
        <p class="text-sm">No motorcycles match your criteria.</p>
      </div>
    `;
    return;
  }

  feed.innerHTML = filtered.map(renderBikeCard).join("");
}

// 2. Explore Grid
function renderExploreGrid() {
  const grid = document.getElementById("exploreBikeGrid");
  const badge = document.getElementById("exploreCountBadge");
  if (!grid) return;

  badge.innerText = `${motorcycles.length} Bikes`;
  grid.innerHTML = motorcycles.map(renderBikeCard).join("");
}

// 3. Brands Grid
function renderBrandsGrid() {
  const grid = document.getElementById("brandsGrid");
  if (!grid) return;

  grid.innerHTML = BRANDS.map(brand => {
    const brandBikes = motorcycles.filter(b => b.brand.toLowerCase() === brand.name.toLowerCase());
    return `
      <div class="bg-slate850 border border-slate800 rounded-2xl p-5 hover:border-slate700 transition space-y-3">
        <div class="flex items-center justify-between">
          <div class="w-10 h-10 rounded-xl bg-slate900 border border-slate800 flex items-center justify-center text-cyanNeon text-lg">
            <i class="fa-solid ${brand.icon}"></i>
          </div>
          <span class="text-[10px] font-bold text-slate-400 bg-slate900 border border-slate800 px-2.5 py-1 rounded-full uppercase">
            ${brand.country} • Est. ${brand.year}
          </span>
        </div>
        <div>
          <h3 class="text-base font-bold text-white">${brand.name}</h3>
          <p class="text-xs text-cyanNeon font-semibold">${brandBikes.length} Models in Catalog</p>
        </div>
        <button onclick="filterByBrand('${brand.name}')" class="w-full bg-slate900 hover:bg-slate800 text-slate-300 font-bold py-2 rounded-xl text-xs transition border border-slate800">
          View ${brand.name} Models
        </button>
      </div>
    `;
  }).join("");
}

function filterByBrand(brandName) {
  currentSearch = brandName;
  currentCategory = "All";
  switchTab("home");
  const input = document.getElementById("homeSearchInput");
  if (input) input.value = brandName;
}

// 4. Compare View
function renderCompareView() {
  const container = document.getElementById("compareContainer");
  if (!container) return;

  const bikes = compareSlots.map(id => motorcycles.find(m => m.id === id)).filter(Boolean);

  if (bikes.length === 0) {
    container.innerHTML = `
      <div class="bg-slate900 border border-slate800 rounded-3xl p-8 text-center space-y-4 max-w-md mx-auto">
        <div class="w-16 h-16 rounded-2xl bg-cyan-500/10 text-cyanNeon flex items-center justify-center mx-auto text-2xl">
          <i class="fa-solid fa-arrow-right-arrow-left"></i>
        </div>
        <h3 class="text-base font-bold text-white">No Bikes Selected for Comparison</h3>
        <p class="text-xs text-slate-400">Tap "Compare" on any motorcycle card to evaluate power, weight, and price side-by-side.</p>
        <button onclick="switchTab('home')" class="bg-cyanNeon text-slate950 font-bold px-4 py-2 rounded-xl text-xs">
          Browse Catalog
        </button>
      </div>
    `;
    return;
  }

  container.innerHTML = `
    <!-- Bike Cards Header -->
    <div class="grid grid-cols-${bikes.length} gap-3">
      ${bikes.map(b => `
        <div class="bg-slate850 border border-slate800 rounded-2xl p-3 relative space-y-2 text-center">
          <button onclick="toggleCompare('${b.id}')" class="absolute top-2 right-2 w-6 h-6 rounded-full bg-slate900 text-slate-400 hover:text-red-400 flex items-center justify-center text-xs">
            <i class="fa-solid fa-xmark"></i>
          </button>
          <img src="${b.img}" alt="${b.name}" class="w-full h-24 object-cover rounded-xl">
          <h4 class="font-bold text-white text-xs truncate">${b.name}</h4>
          <span class="text-trackGreen font-bold text-xs block">${b.priceDisplay}</span>
        </div>
      `).join("")}
    </div>

    <!-- Shootout Table -->
    <div class="bg-slate900 border border-slate800 rounded-2xl overflow-hidden text-xs">
      <div class="p-3 bg-slate850 font-bold text-cyanNeon uppercase tracking-wider text-[11px] border-b border-slate800">
        Engine & Performance Metrics
      </div>
      <table class="w-full text-left">
        <tbody class="divide-y divide-slate800/60">
          <tr class="p-3">
            <td class="p-3 text-slate-400 font-semibold w-1/4">Horsepower</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.powerHp} HP</td>`).join("")}
          </tr>
          <tr>
            <td class="p-3 text-slate-400 font-semibold">Displacement</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.engineCc} cc</td>`).join("")}
          </tr>
          <tr>
            <td class="p-3 text-slate-400 font-semibold">Kerb Weight</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.weightKg} kg</td>`).join("")}
          </tr>
          <tr>
            <td class="p-3 text-slate-400 font-semibold">Mileage</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.mileage || '32.0'} km/l</td>`).join("")}
          </tr>
          <tr>
            <td class="p-3 text-slate-400 font-semibold">Top Speed</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.topSpeed || '160'} km/h</td>`).join("")}
          </tr>
        </tbody>
      </table>
    </div>

    <!-- AI Verdict Box -->
    <div class="bg-gradient-to-r from-cyan-950/40 via-slate900 to-slate850 border border-cyan-500/30 rounded-2xl p-5 space-y-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <i class="fa-solid fa-robot text-cyanNeon"></i>
          <h4 class="font-bold text-white text-xs">Gemini AI Buyer Verdict</h4>
        </div>
        <button id="aiVerdictBtn" onclick="getAiCompareVerdict()" class="bg-cyanNeon text-slate950 font-bold px-3 py-1 rounded-xl text-xs hover:bg-cyan-300 transition">
          Generate AI Verdict
        </button>
      </div>
      <div id="aiVerdictBox" class="text-xs text-slate-300 leading-relaxed">
        Click "Generate AI Verdict" to let Google Gemini 2.5 Flash analyze dyno curves and give an unbiased purchasing decision.
      </div>
    </div>
  `;
}

// 5. Saved / Bookmarks View
function renderSavedFeed() {
  const feed = document.getElementById("savedFeed");
  const count = document.getElementById("savedCountBadge");
  if (!feed) return;

  const favs = savedBikes.map(id => motorcycles.find(m => m.id === id)).filter(Boolean);
  count.innerText = `${favs.length} Saved`;

  if (favs.length === 0) {
    feed.innerHTML = `
      <div class="text-center py-12 text-slate-500 space-y-3">
        <i class="fa-regular fa-bookmark text-3xl"></i>
        <h4 class="text-sm font-bold text-slate-300">No Saved Motorcycles</h4>
        <p class="text-xs">Tap the bookmark icon on any motorcycle to access it quickly offline.</p>
      </div>
    `;
    return;
  }

  feed.innerHTML = favs.map(renderBikeCard).join("");
}

// 6. Studio / Admin View (Matches Vivo Studio tab 1:1)
function renderStudioView() {
  const count = document.getElementById("studioCount");
  const list = document.getElementById("studioBikesList");
  if (!list) return;

  count.innerText = motorcycles.length;

  list.innerHTML = motorcycles.map(bike => `
    <div class="bg-slate850 border border-slate800 rounded-xl p-3 flex items-center justify-between gap-3 hover:border-slate700 transition">
      <div class="flex items-center gap-3 truncate">
        <img src="${bike.img}" alt="${bike.name}" class="w-12 h-12 object-cover rounded-lg shrink-0">
        <div class="truncate">
          <div class="flex items-center gap-2">
            <span class="font-bold text-white text-xs truncate">${bike.brand} • ${bike.name}</span>
          </div>
          <p class="text-[11px] text-slate-400">${bike.category} | ${bike.powerHp} HP | ${bike.priceDisplay}</p>
        </div>
      </div>
      <button onclick="deleteBike('${bike.id}')" title="Delete Model" class="w-8 h-8 rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500 hover:text-white flex items-center justify-center shrink-0 transition">
        <i class="fa-solid fa-trash-can text-xs"></i>
      </button>
    </div>
  `).join("");
}

// Comparison Slot Bar Management (Matches Vivo Floating Bar 1:1)
function updateComparisonSlotBar() {
  const bar = document.getElementById("comparisonSlotBar");
  const container = document.getElementById("slotThumbnails");
  const countSpan = document.getElementById("slotCount");
  const topBadge = document.getElementById("topCompareBadge");
  const bottomBadge = document.getElementById("bottomCompareBadge");

  const count = compareSlots.length;

  // Update badges
  if (topBadge) {
    topBadge.innerText = count;
    topBadge.classList.toggle("hidden", count === 0);
  }
  if (bottomBadge) {
    bottomBadge.innerText = count;
    bottomBadge.classList.toggle("hidden", count === 0);
  }

  if (count === 0 || currentTab === "compare") {
    if (bar) bar.classList.add("translate-y-32");
    return;
  }

  if (bar) bar.classList.remove("translate-y-32");
  if (countSpan) countSpan.innerText = count;

  const bikes = compareSlots.map(id => motorcycles.find(m => m.id === id)).filter(Boolean);

  let html = "";
  bikes.forEach((bike, index) => {
    html += `
      <div class="relative w-11 h-11 rounded-lg overflow-hidden border border-cyanNeon/50 shrink-0">
        <img src="${bike.img}" alt="${bike.name}" class="w-full h-full object-cover">
        <button onclick="toggleCompare('${bike.id}')" class="absolute top-0 right-0 w-3.5 h-3.5 bg-slate950/80 text-white hover:text-red-400 flex items-center justify-center text-[8px]">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
    `;
    if (index < bikes.length - 1) {
      html += `<span class="text-[10px] font-black text-cyanNeon">VS</span>`;
    }
  });

  if (container) container.innerHTML = html;
}

// Compare Actions
function toggleCompare(bikeId) {
  const index = compareSlots.indexOf(bikeId);
  if (index > -1) {
    compareSlots.splice(index, 1);
  } else {
    if (compareSlots.length >= 3) {
      alert("You can compare up to 3 motorcycles at once.");
      return;
    }
    compareSlots.push(bikeId);
  }
  localStorage.setItem("motoscope_compare", JSON.stringify(compareSlots));
  renderCurrentTab();
  updateComparisonSlotBar();
}

function clearComparison() {
  compareSlots = [];
  localStorage.removeItem("motoscope_compare");
  renderCurrentTab();
  updateComparisonSlotBar();
}

// Bookmarks Action
function toggleFavorite(bikeId) {
  const index = savedBikes.indexOf(bikeId);
  if (index > -1) {
    savedBikes.splice(index, 1);
  } else {
    savedBikes.push(bikeId);
  }
  localStorage.setItem("motoscope_saved", JSON.stringify(savedBikes));
  renderCurrentTab();
}

// Search and Category Filters
function filterCategory(cat) {
  currentCategory = cat;
  document.querySelectorAll(".cat-pill").forEach(p => {
    if (p.innerText === cat || (cat === "All" && p.innerText === "All Models")) {
      p.className = "cat-pill active px-3.5 py-1.5 rounded-xl text-xs font-semibold bg-cyanNeon text-slate950 whitespace-nowrap transition";
    } else {
      p.className = "cat-pill px-3.5 py-1.5 rounded-xl text-xs font-semibold bg-slate900 border border-slate800 text-slate-300 hover:border-cyanNeon whitespace-nowrap transition";
    }
  });
  renderHomeFeed();
}

function handleSearch(query) {
  currentSearch = query.trim();
  renderHomeFeed();
}

function handleExploreSearch(query) {
  const grid = document.getElementById("exploreBikeGrid");
  if (!grid) return;

  const q = query.toLowerCase().trim();
  const filtered = motorcycles.filter(b => 
    b.name.toLowerCase().includes(q) || 
    b.brand.toLowerCase().includes(q) || 
    b.category.toLowerCase().includes(q) ||
    b.engineCc.toString().includes(q)
  );

  grid.innerHTML = filtered.map(renderBikeCard).join("");
}

// Studio: Add Motorcycle
function handleAddNewBike() {
  const name = document.getElementById("newModelName").value.trim();
  const brand = document.getElementById("newBrand").value.trim();
  const category = document.getElementById("newCategory").value;
  const price = document.getElementById("newPrice").value.trim();
  const power = parseFloat(document.getElementById("newPower").value) || 25;
  const engine = parseInt(document.getElementById("newEngine").value) || 250;
  const weight = parseInt(document.getElementById("newWeight").value) || 160;
  const img = document.getElementById("newImg").value.trim() || "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80";

  if (!name || !brand || !price) {
    alert("Please fill in model name, brand, and price.");
    return;
  }

  const id = `${brand.toLowerCase()}-${name.toLowerCase().replace(/\s+/g, '-')}-${Date.now()}`;
  const newBike = {
    id,
    name,
    brand,
    category,
    year: 2025,
    priceInr: 200000,
    priceDisplay: price,
    powerHp: power,
    engineCc: engine,
    weightKg: weight,
    mileage: 30.0,
    topSpeed: 150,
    rating: 4.8,
    img,
    desc: "Custom added model in Studio."
  };

  motorcycles.unshift(newBike);
  localStorage.setItem("motoscope_bikes", JSON.stringify(motorcycles));

  // Reset inputs
  document.getElementById("newModelName").value = "";
  document.getElementById("newBrand").value = "";
  document.getElementById("newPrice").value = "";
  document.getElementById("newPower").value = "";
  document.getElementById("newEngine").value = "";
  document.getElementById("newWeight").value = "";
  document.getElementById("newImg").value = "";

  renderStudioView();
  alert(`Motorcycle ${name} added successfully!`);
}

// Studio: Delete Motorcycle
function deleteBike(id) {
  motorcycles = motorcycles.filter(b => b.id !== id);
  compareSlots = compareSlots.filter(s => s !== id);
  savedBikes = savedBikes.filter(s => s !== id);

  localStorage.setItem("motoscope_bikes", JSON.stringify(motorcycles));
  localStorage.setItem("motoscope_compare", JSON.stringify(compareSlots));
  localStorage.setItem("motoscope_saved", JSON.stringify(savedBikes));

  renderStudioView();
}

// Reset Database Dialog
function promptResetDatabase() {
  document.getElementById("resetModal").classList.remove("hidden");
}

function closeResetModal() {
  document.getElementById("resetModal").classList.add("hidden");
}

function executeResetDatabase() {
  motorcycles = [...DEFAULT_MOTORCYCLES];
  compareSlots = [];
  savedBikes = [];

  localStorage.setItem("motoscope_bikes", JSON.stringify(motorcycles));
  localStorage.removeItem("motoscope_compare");
  localStorage.removeItem("motoscope_saved");

  closeResetModal();
  renderCurrentTab();
  updateComparisonSlotBar();
  alert("Database restored to default verified models!");
}

// Google Gemini AI Buying Advisor
function openAiAdvisorModal() {
  document.getElementById("aiAdvisorModal").classList.remove("hidden");
}

function closeAiAdvisorModal() {
  document.getElementById("aiAdvisorModal").classList.add("hidden");
}

async function askAiAdvisor() {
  const input = document.getElementById("aiInput");
  const chatBox = document.getElementById("chatBox");
  const query = input.value.trim();
  if (!query) return;

  chatBox.innerHTML += `
    <div class="flex justify-end">
      <div class="bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 text-xs md:text-sm p-3 rounded-2xl rounded-tr-none max-w-[80%]">
        ${query}
      </div>
    </div>
  `;
  input.value = "";
  chatBox.scrollTop = chatBox.scrollHeight;

  const loadId = `loading-${Date.now()}`;
  chatBox.innerHTML += `
    <div id="${loadId}" class="flex gap-2 items-center text-xs text-cyanNeon">
      <span class="w-2 h-2 rounded-full bg-cyanNeon animate-ping"></span>
      MotoScope Gemini AI is thinking...
    </div>
  `;
  chatBox.scrollTop = chatBox.scrollHeight;

  try {
    const summary = motorcycles.map(b => `- ${b.brand} ${b.name} (${b.category}, ${b.powerHp}HP, ${b.engineCc}cc, ${b.priceDisplay})`).join("\n");
    const systemPrompt = `You are MotoScope AI, an expert motorcycle engineer. Database:\n${summary}\nAnswer concisely and helpfully in bullet points.`;

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
      <div class="text-xs text-red-400 bg-red-500/10 p-3 rounded-xl">
        Error communicating with Gemini: ${err.message}
      </div>
    `;
  }
  chatBox.scrollTop = chatBox.scrollHeight;
}

// Compare Verdict
async function getAiCompareVerdict() {
  const btn = document.getElementById("aiVerdictBtn");
  const box = document.getElementById("aiVerdictBox");
  const bikes = compareSlots.map(id => motorcycles.find(m => m.id === id)).filter(Boolean);

  if (bikes.length === 0) return;

  btn.disabled = true;
  btn.innerText = "Analyzing...";
  box.innerHTML = `<span class="inline-flex items-center gap-2 text-cyanNeon"><span class="w-2 h-2 rounded-full bg-cyanNeon animate-ping"></span> Gemini AI analyzing comparison...</span>`;

  try {
    const summary = bikes.map(b => `${b.brand} ${b.name}: ${b.powerHp}HP, ${b.engineCc}cc, ${b.weightKg}kg, ${b.priceDisplay}`).join(" vs ");
    const prompt = `Compare these motorcycles head-to-head for a buyer: ${summary}. Provide: 1. Outright Winner, 2. Best for City, 3. Best for Touring, 4. Value for Money. Keep it punchy in bullet points.`;

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
  btn.innerText = "Generate AI Verdict";
}

// EMI Calculator
function openEmiModal() {
  document.getElementById("emiModal").classList.remove("hidden");
  calculateEmi();
}

function closeEmiModal() {
  document.getElementById("emiModal").classList.add("hidden");
}

function calculateEmi() {
  const price = parseFloat(document.getElementById("emiPriceRange").value);
  const down = parseFloat(document.getElementById("emiDownRange").value);
  const rate = parseFloat(document.getElementById("emiRateRange").value);
  const tenure = parseInt(document.getElementById("emiTenureRange").value);

  document.getElementById("emiPriceVal").innerText = `₹${price.toLocaleString("en-IN")}`;
  document.getElementById("emiDownVal").innerText = `₹${down.toLocaleString("en-IN")}`;
  document.getElementById("emiRateVal").innerText = `${rate}%`;
  document.getElementById("emiTenureVal").innerText = `${tenure} Months`;

  const principal = Math.max(0, price - down);
  const monthlyRate = (rate / 12) / 100;
  let emi = 0;
  if (monthlyRate > 0) {
    emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenure)) / (Math.pow(1 + monthlyRate, tenure) - 1);
  } else {
    emi = principal / tenure;
  }

  const totalPayable = emi * tenure;
  const totalInterest = Math.max(0, totalPayable - principal);

  document.getElementById("emiResultText").innerText = `₹${Math.round(emi).toLocaleString("en-IN")} / mo`;
  document.getElementById("emiTotalPayable").innerText = `Total Loan Interest: ₹${Math.round(totalInterest).toLocaleString("en-IN")}`;
}

// Bootstrapping
window.addEventListener("DOMContentLoaded", () => {
  initData();
  switchTab("home");
});
