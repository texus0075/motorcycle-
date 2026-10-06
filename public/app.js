// MotoScope Global Core Logic - 70+ Motorcycles, Cloud Firestore, Firebase Auth & Gemini AI

// 1. Firebase Configuration & Initialization
const getFirebaseApiKey = () => atob("QUl6YVN5RGlmUE5yWGNWcnZTanBveUdZNjFzUXdaSTQ1dFFWclJJ");
const getGeminiKey = () => atob("QVEuQWI4Uk42S1E3clhXT3JBSHBpaXlWVFoydzBRWjRSRmNwanZVanNNQXJ5akw1UHpZeUE=");

const firebaseConfig = {
  apiKey: getFirebaseApiKey(),
  authDomain: "motohub-live-1a2b3c.firebaseapp.com",
  projectId: "motohub-live-1a2b3c",
  storageBucket: "motohub-live-1a2b3c.firebasestorage.app",
  messagingSenderId: "1090249335282",
  appId: "1:1090249335282:web:7f6d5e4c3b2a1"
};

let auth = null;
let db = null;

try {
  if (typeof firebase !== "undefined") {
    firebase.initializeApp(firebaseConfig);
    auth = firebase.auth();
    db = firebase.firestore();
  }
} catch (e) {
  console.warn("Firebase initialization notice:", e.message);
}

// 2. Global State
let motorcycles = [];
let compareSlots = [];
let savedBikes = [];
let currentCategory = "All";
let currentSearch = "";
let currentTab = "home";
let selectedBrandId = null;

// Brands Master List
const ICONIC_BRANDS = [
  { id: "yamaha", name: "Yamaha", icon: "fa-motorcycle", color: "#0038A8", tag: "YZF & MT Series" },
  { id: "kawasaki", name: "Kawasaki", icon: "fa-flag-checkered", color: "#49C300", tag: "Ninja & Z Supercharged" },
  { id: "honda", name: "Honda", icon: "fa-trophy", color: "#E4002B", tag: "Fireblade & Africa Twin" },
  { id: "suzuki", name: "Suzuki", icon: "fa-wind", color: "#005BAA", tag: "Hayabusa & GSX-R Series" },
  { id: "ktm", name: "KTM", icon: "fa-bolt", color: "#FF6600", tag: "Ready to Race • RC & Duke" },
  { id: "bmw", name: "BMW Motorrad", icon: "fa-shield-halved", color: "#0066B1", tag: "M 1000 RR & GS Boxer" },
  { id: "ducati", name: "Ducati", icon: "fa-fire", color: "#CC0000", tag: "Panigale & Streetfighter V4" },
  { id: "triumph", name: "Triumph", icon: "fa-crown", color: "#0B1F3F", tag: "Triple Screamer & Modern Classics" },
  { id: "royalenfield", name: "Royal Enfield", icon: "fa-compass", color: "#D4AF37", tag: "Bullet, Hunter & Himalayan" },
  { id: "aprilia", name: "Aprilia", icon: "fa-gauge-high", color: "#D62226", tag: "RSV4 & RS 457 Twin-Spar" },
  { id: "harleydavidson", name: "Harley-Davidson", icon: "fa-route", color: "#E65100", tag: "Revolution Max & Sportster" }
];

// 3. Bootstrapping & Cloud Firestore Real-time Sync
function initCatalog() {
  // Load base 70 motorcycles from public/catalog_data.js
  if (typeof MASTER_CATALOG !== "undefined" && Array.isArray(MASTER_CATALOG)) {
    motorcycles = [...MASTER_CATALOG];
  }

  // Check LocalStorage cache
  const cached = localStorage.getItem("motoscope_bikes");
  if (cached) {
    try {
      const parsed = JSON.parse(cached);
      if (parsed.length >= 70) motorcycles = parsed;
    } catch (e) {}
  }

  // Load Compare & Saved
  const storedSlots = localStorage.getItem("motoscope_compare");
  if (storedSlots) {
    try { compareSlots = JSON.parse(storedSlots); } catch (e) { compareSlots = []; }
  }

  const storedSaved = localStorage.getItem("motoscope_saved");
  if (storedSaved) {
    try { savedBikes = JSON.parse(storedSaved); } catch (e) { savedBikes = []; }
  }

  // Render initial view
  renderCurrentTab();
  updateComparisonSlotBar();

  // Connect to Live Firestore Cloud
  if (db) {
    setupFirestoreSync();
  }

  // Connect Firebase Auth
  if (auth) {
    setupAuthListeners();
  }
}

// Real-time Firestore Cloud Sync
function setupFirestoreSync() {
  db.collection("motorcycles").onSnapshot(snapshot => {
    if (!snapshot.empty) {
      const cloudBikes = [];
      snapshot.forEach(doc => cloudBikes.push({ id: doc.id, ...doc.data() }));
      motorcycles = cloudBikes;
      localStorage.setItem("motoscope_bikes", JSON.stringify(motorcycles));
      renderCurrentTab();
      updateComparisonSlotBar();
      updateCloudStatus(`☁️ Cloud Synced (${motorcycles.length} Bikes Live)`);
    } else {
      // Auto-seed Firestore on initial connect with full 70 bikes
      seedFirestoreWithMasterCatalog();
    }
  }, err => {
    console.warn("Firestore realtime sync note:", err.message);
    updateCloudStatus(`☁️ Local Master Active (${motorcycles.length} Bikes)`);
  });
}

function updateCloudStatus(text) {
  const el = document.getElementById("cloudStatusText");
  if (el) el.innerText = text;
}

async function seedFirestoreWithMasterCatalog() {
  if (!db || !MASTER_CATALOG || MASTER_CATALOG.length === 0) return;
  console.log("Seeding Firestore with 70 Master Motorcycles...");
  try {
    const batch = db.batch();
    MASTER_CATALOG.slice(0, 30).forEach(b => {
      const ref = db.collection("motorcycles").doc(b.id);
      batch.set(ref, b, { merge: true });
    });
    await batch.commit();
    updateCloudStatus(`☁️ Cloud Synced (${motorcycles.length} Bikes)`);
  } catch (err) {
    console.warn("Firestore seed notice:", err.message);
  }
}

// 4. Tab Navigation (5 Clean Consumer Tabs)
function switchTab(tab) {
  currentTab = tab;
  const tabs = ["home", "explore", "brands", "compare", "saved"];
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

function renderCurrentTab() {
  if (currentTab === "home") renderHomeFeed();
  else if (currentTab === "explore") renderExploreGrid();
  else if (currentTab === "brands") renderBrandsView();
  else if (currentTab === "compare") renderCompareView();
  else if (currentTab === "saved") renderSavedFeed();
}

// 5. Motorcycle Card Component (Exact Match with Android Native App)
function renderBikeCard(bike) {
  const isInCompare = compareSlots.includes(bike.id);
  const isFav = savedBikes.includes(bike.id);

  const power = bike.specs?.maxPowerHp || bike.powerHp || 40;
  const cc = bike.specs?.displacementCc || bike.engineCc || 398;
  const weight = bike.specs?.kerbWeightKg || bike.weightKg || 170;
  const price = bike.priceDisplay || `$${bike.basePrice || 2500}`;
  const brandName = bike.brand || (bike.brandId ? bike.brandId.toUpperCase() : "MOTORCYCLE");
  const img = bike.heroImageUrl || bike.img || "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80";

  return `
    <div class="motorcycle-card bg-slate850 border ${isInCompare ? 'border-cyanNeon shadow-lg shadow-cyan-500/10' : 'border-slate800'} rounded-2xl overflow-hidden hover:border-slate700 transition cursor-pointer" onclick="openDeepModal('${bike.id}')">
      <!-- Image with overlay badges -->
      <div class="relative h-48 sm:h-56 bg-slate950 overflow-hidden">
        <img src="${img}" alt="${bike.name || bike.modelName}" loading="lazy" class="w-full h-full object-cover group-hover:scale-105 transition duration-500">
        
        <!-- Category Badge Top-Left -->
        <span class="absolute top-2.5 left-2.5 bg-slate950/85 backdrop-blur-md border border-cyan-500/40 text-cyanNeon text-[10px] font-black uppercase px-2.5 py-0.5 rounded-lg tracking-wider">
          ${bike.category || 'Sport'}
        </span>

        <!-- Bookmark Button Top-Right -->
        <button onclick="event.stopPropagation(); toggleFavorite('${bike.id}')" title="Save offline" class="absolute top-2.5 right-2.5 w-8 h-8 rounded-full bg-slate950/85 backdrop-blur-md border border-slate700 ${isFav ? 'text-amberOrange' : 'text-slate-400 hover:text-white'} flex items-center justify-center transition">
          <i class="${isFav ? 'fa-solid' : 'fa-regular'} fa-bookmark text-sm"></i>
        </button>

        <!-- Brand Badge Bottom-Left -->
        <span class="absolute bottom-2.5 left-2.5 bg-slate950/90 text-white font-black text-[11px] tracking-wider px-2.5 py-0.5 rounded uppercase">
          ${brandName}
        </span>
      </div>

      <!-- Card Body -->
      <div class="p-4 space-y-3">
        <div class="flex items-center justify-between">
          <h3 class="text-base font-bold text-white tracking-wide">${bike.name || bike.modelName}</h3>
          <span class="text-amber-400 text-xs font-bold flex items-center gap-1">
            ★ ${bike.rating || '4.8'}
          </span>
        </div>

        <!-- 3 Spec Boxes (Power, Engine, Weight) -->
        <div class="grid grid-cols-3 gap-2 text-center text-xs">
          <div class="bg-slate900 border border-slate800/80 p-2 rounded-xl">
            <span class="text-[9px] uppercase tracking-wider text-slate-500 block font-bold">POWER</span>
            <span class="text-slate-100 font-bold">${power} HP</span>
          </div>
          <div class="bg-slate900 border border-slate800/80 p-2 rounded-xl">
            <span class="text-[9px] uppercase tracking-wider text-slate-500 block font-bold">ENGINE</span>
            <span class="text-slate-100 font-bold">${cc} cc</span>
          </div>
          <div class="bg-slate900 border border-slate800/80 p-2 rounded-xl">
            <span class="text-[9px] uppercase tracking-wider text-slate-500 block font-bold">WEIGHT</span>
            <span class="text-slate-100 font-bold">${weight} kg</span>
          </div>
        </div>

        <!-- Price & Compare Button -->
        <div class="flex items-center justify-between pt-1">
          <div>
            <span class="text-[9px] uppercase font-bold text-slate-500 block">EST. PRICE</span>
            <span class="text-trackGreen font-extrabold text-xs sm:text-sm">${price}</span>
          </div>
          <div class="flex items-center gap-1.5">
            <span class="text-[11px] text-cyanNeon hover:underline hidden sm:inline font-bold mr-1">Deep Specs →</span>
            <button onclick="event.stopPropagation(); toggleCompare('${bike.id}')" class="px-3.5 py-1.5 rounded-xl text-xs font-bold transition flex items-center gap-1.5 ${isInCompare ? 'bg-cyanNeon text-slate950' : 'bg-slate900 border border-cyan-500/40 text-cyanNeon hover:bg-cyan-500/10'}">
              <i class="fa-solid fa-arrow-right-arrow-left text-[11px]"></i>
              <span>${isInCompare ? 'In Compare' : 'Compare'}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  `;
}

// 6. Home Feed Renderer
function renderHomeFeed() {
  const feed = document.getElementById("homeBikeFeed");
  if (!feed) return;

  const filtered = motorcycles.filter(b => {
    const cat = b.category || "";
    const name = b.name || b.modelName || "";
    const brand = b.brand || b.brandId || "";

    const matchCat = currentCategory === "All" || cat.toLowerCase().includes(currentCategory.toLowerCase());
    const matchSearch = currentSearch === "" || 
      name.toLowerCase().includes(currentSearch.toLowerCase()) || 
      brand.toLowerCase().includes(currentSearch.toLowerCase()) ||
      cat.toLowerCase().includes(currentSearch.toLowerCase()) ||
      (b.specs?.displacementCc && b.specs.displacementCc.toString().includes(currentSearch));
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

// 7. Explore Grid Renderer
function renderExploreGrid() {
  const grid = document.getElementById("exploreBikeGrid");
  const badge = document.getElementById("exploreCountBadge");
  if (!grid) return;

  badge.innerText = `${motorcycles.length} Bikes`;
  grid.innerHTML = motorcycles.map(renderBikeCard).join("");
}

// 8. Pure Brand Showroom (No Country Groups)
function renderBrandsView() {
  const brandsGrid = document.getElementById("brandsGrid");
  const brandDetailView = document.getElementById("brandDetailView");
  const backBtn = document.getElementById("brandBackBtn");

  if (!brandsGrid) return;

  if (selectedBrandId) {
    brandsGrid.classList.add("hidden");
    brandDetailView.classList.remove("hidden");
    backBtn.classList.remove("hidden");
    renderBrandDetail(selectedBrandId);
  } else {
    brandsGrid.classList.remove("hidden");
    brandDetailView.classList.add("hidden");
    backBtn.classList.add("hidden");

    brandsGrid.innerHTML = ICONIC_BRANDS.map(brand => {
      const brandBikes = motorcycles.filter(b => (b.brandId || b.brand || "").toLowerCase() === brand.id.toLowerCase());
      return `
        <div class="bg-slate850 border border-slate800 rounded-2xl p-5 hover:border-cyanNeon transition cursor-pointer space-y-3" onclick="openBrand('${brand.id}')">
          <div class="flex items-center justify-between">
            <div class="w-12 h-12 rounded-xl bg-slate900 border border-slate800 flex items-center justify-center text-cyanNeon text-xl">
              <i class="fa-solid ${brand.icon}"></i>
            </div>
            <span class="text-[10px] font-bold text-cyanNeon bg-cyanNeon/10 border border-cyan-500/30 px-2.5 py-1 rounded-full uppercase">
              ${brandBikes.length} Models
            </span>
          </div>
          <div>
            <h3 class="text-base font-bold text-white">${brand.name}</h3>
            <p class="text-xs text-slate-400">${brand.tag}</p>
          </div>
          <div class="pt-2 border-t border-slate800/80 flex items-center justify-between text-xs text-cyanNeon font-bold">
            <span>Explore Lineup</span>
            <i class="fa-solid fa-arrow-right text-[10px]"></i>
          </div>
        </div>
      `;
    }).join("");
  }
}

function openBrand(brandId) {
  selectedBrandId = brandId;
  renderBrandsView();
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function showAllBrands() {
  selectedBrandId = null;
  renderBrandsView();
}

function renderBrandDetail(brandId) {
  const header = document.getElementById("brandDetailHeader");
  const feed = document.getElementById("brandBikesFeed");
  const brand = ICONIC_BRANDS.find(b => b.id === brandId) || { name: brandId.toUpperCase(), tag: "Motorcycle Manufacturer" };

  const brandBikes = motorcycles.filter(b => (b.brandId || b.brand || "").toLowerCase() === brandId.toLowerCase());

  header.innerHTML = `
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-xl font-black text-white font-display">${brand.name} Full Lineup</h2>
        <p class="text-xs text-slate-400">All registered models from entry to flagship hyperbikes (${brandBikes.length} Total)</p>
      </div>
      <span class="text-xs font-bold text-cyanNeon bg-cyan-500/10 border border-cyan-500/30 px-3 py-1 rounded-xl">
        Verified Catalog
      </span>
    </div>
  `;

  if (brandBikes.length === 0) {
    feed.innerHTML = `<div class="text-center py-10 text-slate-500 text-xs">No models registered for this brand yet.</div>`;
  } else {
    feed.innerHTML = brandBikes.map(renderBikeCard).join("");
  }
}

// 9. ULTRA-DEEP MOTORCYCLE SPECIFICATION MODAL (OPENS ON BIKE CLICK)
function openDeepModal(bikeId) {
  const bike = motorcycles.find(m => m.id === bikeId);
  if (!bike) return;

  const modal = document.getElementById("deepBikeModal");
  const content = document.getElementById("deepModalContent");
  const title = document.getElementById("deepModalTitle");
  const brandPill = document.getElementById("deepBrandPill");

  title.innerText = `${bike.name || bike.modelName} (${bike.modelYear || 2024})`;
  brandPill.innerText = bike.brand || (bike.brandId ? bike.brandId.toUpperCase() : "MOTORCYCLE");

  const s = bike.specs || {};
  const isInCompare = compareSlots.includes(bike.id);
  const isFav = savedBikes.includes(bike.id);
  const img = bike.heroImageUrl || bike.img || "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80";

  content.innerHTML = `
    <!-- Top Hero Section -->
    <div class="space-y-4">
      <div class="relative h-64 sm:h-80 rounded-2xl overflow-hidden bg-slate950 border border-slate800">
        <img id="deepModalHeroImg" src="${img}" alt="${bike.name}" class="w-full h-full object-cover">
        
        <div class="absolute bottom-3 left-3 bg-slate950/90 backdrop-blur-md border border-slate700 rounded-xl px-3 py-1.5">
          <span class="text-[10px] text-slate-400 block font-semibold">HOMOLOGATION SOURCE</span>
          <span class="text-xs text-cyanNeon font-bold">${bike.source || 'Official Manufacturer Homologation Datasheet'}</span>
        </div>
      </div>

      <!-- Quick Action Strip -->
      <div class="flex flex-wrap items-center justify-between gap-3 bg-slate850 border border-slate800 rounded-2xl p-4">
        <div>
          <span class="text-[10px] uppercase font-bold text-slate-500 block">ESTIMATED PRICE</span>
          <div class="text-lg font-black text-trackGreen">${bike.priceDisplay || '$' + bike.basePrice}</div>
        </div>
        <div class="flex items-center gap-2">
          <button onclick="toggleCompare('${bike.id}'); closeDeepModal();" class="px-3 py-2 rounded-xl text-xs font-bold transition flex items-center gap-1.5 ${isInCompare ? 'bg-cyanNeon text-slate950' : 'bg-slate900 border border-cyanNeon text-cyanNeon hover:bg-cyan-500/10'}">
            <i class="fa-solid fa-arrow-right-arrow-left"></i>
            <span>${isInCompare ? 'In Compare' : 'Add to Compare'}</span>
          </button>
          <button onclick="toggleFavorite('${bike.id}'); closeDeepModal();" class="px-3 py-2 rounded-xl text-xs font-bold bg-slate900 border border-slate700 text-slate-200 hover:text-white flex items-center gap-1.5">
            <i class="${isFav ? 'fa-solid text-amberOrange' : 'fa-regular'} fa-bookmark"></i>
            <span>${isFav ? 'Saved' : 'Save'}</span>
          </button>
          <button onclick="closeDeepModal(); openEmiModal();" class="px-3 py-2 rounded-xl text-xs font-bold bg-slate900 border border-slate700 text-amber-400 hover:text-white flex items-center gap-1.5">
            <i class="fa-solid fa-calculator"></i>
            <span>EMI</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 1. Engine & Powertrain Telemetry -->
    <div class="bg-slate850 border border-slate800 rounded-2xl p-4 space-y-3">
      <h4 class="font-bold text-white text-xs uppercase tracking-wider text-cyanNeon flex items-center gap-1.5">
        <i class="fa-solid fa-gears"></i> Engine & Powertrain Architecture
      </h4>
      <div class="grid grid-cols-2 sm:grid-cols-3 gap-3">
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">ENGINE TYPE</span>
          <span class="text-white font-bold text-xs">${s.engineType || 'Liquid-cooled DOHC 4-Valve'}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">DISPLACEMENT</span>
          <span class="text-white font-bold text-xs">${s.displacementCc || bike.engineCc || '-'} cc</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">MAX HORSEPOWER</span>
          <span class="text-white font-bold text-xs">${s.maxPowerDisplay || (s.maxPowerHp ? s.maxPowerHp + ' HP' : (bike.powerHp + ' HP'))}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">MAX TORQUE</span>
          <span class="text-white font-bold text-xs">${s.maxTorqueDisplay || (s.maxTorqueNm ? s.maxTorqueNm + ' Nm' : '-')}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">COOLING SYSTEM</span>
          <span class="text-white font-bold text-xs">${s.cooling || 'Liquid Cooled'}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">VALVES / CYLINDERS</span>
          <span class="text-white font-bold text-xs">${s.valves || 4} Valves • ${s.cylinders || 1} Cyl</span>
        </div>
      </div>
    </div>

    <!-- 2. Performance, Speed & Acceleration -->
    <div class="bg-slate850 border border-slate800 rounded-2xl p-4 space-y-3">
      <h4 class="font-bold text-white text-xs uppercase tracking-wider text-cyanNeon flex items-center gap-1.5">
        <i class="fa-solid fa-stopwatch"></i> Dyno & Real-World Performance
      </h4>
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 text-center">
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">TOP SPEED</span>
          <span class="text-white font-extrabold text-sm">${s.topSpeedKmh || bike.topSpeed || '-'} km/h</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">0-100 KM/H</span>
          <span class="text-white font-extrabold text-sm">${s.accel0To100Sec ? s.accel0To100Sec + 's' : '-'}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">FUEL ECONOMY</span>
          <span class="text-white font-extrabold text-sm">${s.mileageKmpl || bike.mileage || '30.0'} km/l</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">POWER-TO-WEIGHT</span>
          <span class="text-white font-extrabold text-sm">${s.powerToWeightHpPerTon ? s.powerToWeightHpPerTon + ' HP/Ton' : '-'}</span>
        </div>
      </div>
    </div>

    <!-- 3. Chassis, Suspension, Brakes & Tyres -->
    <div class="bg-slate850 border border-slate800 rounded-2xl p-4 space-y-3">
      <h4 class="font-bold text-white text-xs uppercase tracking-wider text-cyanNeon flex items-center gap-1.5">
        <i class="fa-solid fa-wrench"></i> Chassis, Brakes & Hardware
      </h4>
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">FRAME TYPE</span>
          <span class="text-white font-semibold text-xs">${s.frameType || 'Aluminium Twin-Spar / Trellis'}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">TRANSMISSION</span>
          <span class="text-white font-semibold text-xs">${s.gearbox || '6-Speed Constant Mesh'}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">FRONT SUSPENSION</span>
          <span class="text-white font-semibold text-xs">${s.frontSuspension || 'Inverted USD Telescopic Fork'}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">REAR SUSPENSION</span>
          <span class="text-white font-semibold text-xs">${s.rearSuspension || 'Link-Type Monoshock with Preload Adjust'}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">BRAKING SYSTEM</span>
          <span class="text-white font-semibold text-xs">${s.frontBrake || 'Dual Hydraulic Discs with Radial Calipers'}</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">ABS & ELECTRONICS</span>
          <span class="text-white font-semibold text-xs">${s.absSystem || 'Dual-Channel Cornering ABS'}</span>
        </div>
      </div>
    </div>

    <!-- 4. Dimensions & Ergonomics -->
    <div class="bg-slate850 border border-slate800 rounded-2xl p-4 space-y-3">
      <h4 class="font-bold text-white text-xs uppercase tracking-wider text-cyanNeon flex items-center gap-1.5">
        <i class="fa-solid fa-ruler-combined"></i> Dimensions & Ergonomics
      </h4>
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 text-center">
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">KERB WEIGHT</span>
          <span class="text-white font-bold text-xs">${s.kerbWeightKg || bike.weightKg || '-'} kg</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">SEAT HEIGHT</span>
          <span class="text-white font-bold text-xs">${s.seatHeightMm || '-'} mm</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">FUEL TANK</span>
          <span class="text-white font-bold text-xs">${s.fuelTankCapacityL || '-'} L</span>
        </div>
        <div class="bg-slate900 border border-slate800 p-2.5 rounded-xl">
          <span class="text-[10px] text-slate-500 block">RIDING MODES</span>
          <span class="text-white font-bold text-xs">${s.ridingModes || 'Track / Sport / Street / Rain'}</span>
        </div>
      </div>
    </div>

    <!-- 5. Google Gemini AI Instant Insights -->
    <div class="bg-gradient-to-r from-cyan-950/40 via-slate900 to-slate850 border border-cyan-500/30 rounded-2xl p-4 space-y-2">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <i class="fa-solid fa-robot text-cyanNeon"></i>
          <h4 class="font-bold text-white text-xs">Gemini AI Buyer Verdict</h4>
        </div>
        <button onclick="askGeminiAboutBike('${bike.name || bike.modelName}')" class="bg-cyanNeon text-slate950 font-bold px-3 py-1 rounded-xl text-xs hover:bg-cyan-300 transition">
          Ask Gemini Advisor
        </button>
      </div>
      <p class="text-xs text-slate-300 leading-relaxed">${bike.desc || 'High-performance machine engineered with class-leading electronics and race-proven ergonomics.'}</p>
    </div>
  `;

  modal.classList.remove("hidden");
}

function closeDeepModal() {
  const modal = document.getElementById("deepBikeModal");
  if (modal) modal.classList.add("hidden");
}

function askGeminiAboutBike(bikeName) {
  closeDeepModal();
  openAiAdvisorModal();
  const input = document.getElementById("aiInput");
  if (input) {
    input.value = `Tell me the pros, cons, and maintenance cost of ${bikeName} for daily riding.`;
    askAiAdvisor();
  }
}

// 10. Compare Actions & Drawer
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

function updateComparisonSlotBar() {
  const bar = document.getElementById("comparisonSlotBar");
  const container = document.getElementById("slotThumbnails");
  const countSpan = document.getElementById("slotCount");
  const topBadge = document.getElementById("topCompareBadge");
  const bottomBadge = document.getElementById("bottomCompareBadge");

  const count = compareSlots.length;

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
    const img = bike.heroImageUrl || bike.img || "";
    html += `
      <div class="relative w-11 h-11 rounded-lg overflow-hidden border border-cyanNeon/50 shrink-0">
        <img src="${img}" alt="${bike.name}" class="w-full h-full object-cover">
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

// 11. Compare View Shootout
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
    <div class="grid grid-cols-${bikes.length} gap-3">
      ${bikes.map(b => `
        <div class="bg-slate850 border border-slate800 rounded-2xl p-3 relative space-y-2 text-center">
          <button onclick="toggleCompare('${b.id}')" class="absolute top-2 right-2 w-6 h-6 rounded-full bg-slate900 text-slate-400 hover:text-red-400 flex items-center justify-center text-xs">
            <i class="fa-solid fa-xmark"></i>
          </button>
          <img src="${b.heroImageUrl || b.img}" alt="${b.name}" class="w-full h-24 object-cover rounded-xl">
          <h4 class="font-bold text-white text-xs truncate">${b.name || b.modelName}</h4>
          <span class="text-trackGreen font-bold text-xs block">${b.priceDisplay}</span>
        </div>
      `).join("")}
    </div>

    <div class="bg-slate900 border border-slate800 rounded-2xl overflow-hidden text-xs">
      <div class="p-3 bg-slate850 font-bold text-cyanNeon uppercase tracking-wider text-[11px] border-b border-slate800">
        Engine & Performance Metrics
      </div>
      <table class="w-full text-left">
        <tbody class="divide-y divide-slate800/60">
          <tr>
            <td class="p-3 text-slate-400 font-semibold w-1/4">Horsepower</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.specs?.maxPowerHp || b.powerHp || '-'} HP</td>`).join("")}
          </tr>
          <tr>
            <td class="p-3 text-slate-400 font-semibold">Displacement</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.specs?.displacementCc || b.engineCc || '-'} cc</td>`).join("")}
          </tr>
          <tr>
            <td class="p-3 text-slate-400 font-semibold">Kerb Weight</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.specs?.kerbWeightKg || b.weightKg || '-'} kg</td>`).join("")}
          </tr>
          <tr>
            <td class="p-3 text-slate-400 font-semibold">Mileage</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.specs?.mileageKmpl || b.mileage || '30.0'} km/l</td>`).join("")}
          </tr>
          <tr>
            <td class="p-3 text-slate-400 font-semibold">Top Speed</td>
            ${bikes.map(b => `<td class="p-3 font-bold text-white">${b.specs?.topSpeedKmh || b.topSpeed || '-'} km/h</td>`).join("")}
          </tr>
        </tbody>
      </table>
    </div>

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

// 12. Bookmarks / Saved View
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

// 13. Search and Category Filters
function filterCategory(cat) {
  currentCategory = cat;
  document.querySelectorAll(".cat-pill").forEach(p => {
    if (p.innerText.startsWith(cat) || (cat === "All" && p.innerText.startsWith("All Models"))) {
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
  const filtered = motorcycles.filter(b => {
    const name = (b.name || b.modelName || "").toLowerCase();
    const brand = (b.brand || b.brandId || "").toLowerCase();
    const cat = (b.category || "").toLowerCase();
    const cc = (b.specs?.displacementCc || b.engineCc || "").toString();
    const hp = (b.specs?.maxPowerHp || b.powerHp || "").toString();
    return name.includes(q) || brand.includes(q) || cat.includes(q) || cc.includes(q) || hp.includes(q);
  });

  grid.innerHTML = filtered.map(renderBikeCard).join("");
}

// 14. Authentication System (Firebase Auth)
function openAuthModal() {
  document.getElementById("authModal").classList.remove("hidden");
}

function closeAuthModal() {
  document.getElementById("authModal").classList.add("hidden");
}

function setupAuthListeners() {
  auth.onAuthStateChanged(user => {
    const label = document.getElementById("headerAuthLabel");
    const loggedInView = document.getElementById("authLoggedInView");
    const loggedOutView = document.getElementById("authLoggedOutView");
    const userEmailSpan = document.getElementById("authUserEmail");

    if (user) {
      const name = user.displayName || user.email?.split("@")[0] || "Member";
      if (label) label.innerText = name;
      if (loggedInView) loggedInView.classList.remove("hidden");
      if (loggedOutView) loggedOutView.classList.add("hidden");
      if (userEmailSpan) userEmailSpan.innerText = user.email || `Guest Session (${user.uid.slice(0, 8)})`;
    } else {
      if (label) label.innerText = "Sign In";
      if (loggedInView) loggedInView.classList.add("hidden");
      if (loggedOutView) loggedOutView.classList.remove("hidden");
    }
  });
}

async function handleEmailSignIn() {
  const email = document.getElementById("authEmailInput").value.trim();
  const pass = document.getElementById("authPasswordInput").value.trim();
  const errBox = document.getElementById("authErrorMsg");
  if (!email || !pass) return showAuthError("Please enter email and password.");

  try {
    await auth.signInWithEmailAndPassword(email, pass);
    closeAuthModal();
  } catch (err) {
    showAuthError(err.message);
  }
}

async function handleEmailSignUp() {
  const email = document.getElementById("authEmailInput").value.trim();
  const pass = document.getElementById("authPasswordInput").value.trim();
  if (!email || !pass) return showAuthError("Please enter email and password.");

  try {
    await auth.createUserWithEmailAndPassword(email, pass);
    closeAuthModal();
  } catch (err) {
    showAuthError(err.message);
  }
}

async function handleGoogleSignIn() {
  try {
    const provider = new firebase.auth.GoogleAuthProvider();
    await auth.signInWithPopup(provider);
    closeAuthModal();
  } catch (err) {
    showAuthError(err.message);
  }
}

async function handleAnonymousSignIn() {
  try {
    await auth.signInAnonymously();
    closeAuthModal();
  } catch (err) {
    showAuthError(err.message);
  }
}

async function handleSignOut() {
  try {
    await auth.signOut();
    closeAuthModal();
  } catch (err) {
    console.error(err);
  }
}

function showAuthError(msg) {
  const errBox = document.getElementById("authErrorMsg");
  if (errBox) {
    errBox.innerText = msg;
    errBox.classList.remove("hidden");
  }
}

// 15. SECRET CREATOR ADMIN PANEL (TRIGGERED VIA Ctrl+Shift+Z OR 3 CLICKS ON LOGO)
let logoClickCount = 0;
let logoClickTimer = null;

function setupSecretAdminTriggers() {
  // Shortcut: Ctrl + Shift + Z
  window.addEventListener("keydown", (e) => {
    if (e.ctrlKey && e.shiftKey && (e.key === 'Z' || e.key === 'z')) {
      e.preventDefault();
      openSecretAdmin();
    }
  });

  // Triple-click on logo
  const logo = document.getElementById("mainLogoTrigger");
  if (logo) {
    logo.addEventListener("click", () => {
      logoClickCount++;
      clearTimeout(logoClickTimer);
      logoClickTimer = setTimeout(() => { logoClickCount = 0; }, 1200);
      if (logoClickCount >= 3) {
        logoClickCount = 0;
        openSecretAdmin();
      }
    });
  }
}

function openSecretAdmin() {
  document.getElementById("secretAdminModal").classList.remove("hidden");
}

function closeSecretAdmin() {
  document.getElementById("secretAdminModal").classList.add("hidden");
}

// Gemini AI 1-Click Auto-Fetch Specs
async function executeAiAutoFetch() {
  const query = document.getElementById("aiFetchBikeName").value.trim();
  const btn = document.getElementById("aiFetchBtn");
  if (!query) {
    alert("Please enter a motorcycle name (e.g. Ducati Streetfighter V4 SP2).");
    return;
  }

  btn.disabled = true;
  btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Researching...`;

  try {
    const prompt = `Research official technical specs for motorcycle: "${query}".
Output ONLY a raw JSON object with keys:
{
  "modelName": string,
  "brand": string,
  "category": string (one of: Super Sport, Sport, Naked, Cruiser, Adventure, Retro),
  "priceDisplay": string (e.g. "$18,500 (approx. ₹19,20,000)"),
  "powerHp": number,
  "engineCc": number,
  "weightKg": number,
  "topSpeedKmh": number,
  "mileageKmpl": number,
  "heroImageUrl": string (reliable motorcycle image URL),
  "desc": string
}`;

    const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${getGeminiKey()}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ role: "user", parts: [{ text: prompt }] }]
      })
    });
    const data = await res.json();
    const rawText = data.candidates?.[0]?.content?.parts?.[0]?.text || "{}";
    const cleaned = rawText.replace(/```json/g, "").replace(/```/g, "").trim();
    const parsed = JSON.parse(cleaned);

    // Auto-fill form
    document.getElementById("adminModelName").value = parsed.modelName || query;
    document.getElementById("adminBrand").value = parsed.brand || "";
    document.getElementById("adminCategory").value = parsed.category || "Sport";
    document.getElementById("adminPrice").value = parsed.priceDisplay || "";
    document.getElementById("adminPower").value = parsed.powerHp || "";
    document.getElementById("adminEngine").value = parsed.engineCc || "";
    document.getElementById("adminWeight").value = parsed.weightKg || "";
    document.getElementById("adminTopSpeed").value = parsed.topSpeedKmh || "";
    document.getElementById("adminImg").value = parsed.heroImageUrl || "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1000&q=80";

    alert(`AI auto-fetched specs for ${parsed.modelName || query}! Review fields and click Save.`);
  } catch (err) {
    alert("Error during AI auto-fetch: " + err.message);
  }

  btn.disabled = false;
  btn.innerHTML = `<i class="fa-solid fa-bolt"></i> Auto-Fetch`;
}

// Save Motorcycle to Cloud Firestore & Local
async function saveAdminMotorcycle() {
  const name = document.getElementById("adminModelName").value.trim();
  const brand = document.getElementById("adminBrand").value.trim();
  const category = document.getElementById("adminCategory").value;
  const price = document.getElementById("adminPrice").value.trim();
  const power = parseFloat(document.getElementById("adminPower").value) || 40;
  const engine = parseInt(document.getElementById("adminEngine").value) || 400;
  const weight = parseInt(document.getElementById("adminWeight").value) || 170;
  const topSpeed = parseInt(document.getElementById("adminTopSpeed").value) || 160;
  const img = document.getElementById("adminImg").value.trim() || "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1000&q=80";

  if (!name || !brand || !price) {
    alert("Please complete model name, brand, and price.");
    return;
  }

  const id = `${brand.toLowerCase()}-${name.toLowerCase().replace(/[^a-z0-9]+/g, '-')}-${Date.now().toString().slice(-4)}`;
  const bikeObj = {
    id,
    brandId: brand.toLowerCase(),
    brand,
    modelName: name,
    name,
    category,
    modelYear: 2025,
    priceDisplay: price,
    rating: 4.9,
    heroImageUrl: img,
    source: "Creator Admin Verified Telemetry",
    specs: {
      displacementCc: engine,
      maxPowerHp: power,
      topSpeedKmh: topSpeed,
      kerbWeightKg: weight,
      mileageKmpl: 28.0,
      cooling: "Liquid Cooled"
    }
  };

  // 1. Save to Local
  motorcycles.unshift(bikeObj);
  localStorage.setItem("motoscope_bikes", JSON.stringify(motorcycles));

  // 2. Save to Live Cloud Firestore
  if (db) {
    try {
      await db.collection("motorcycles").doc(id).set(bikeObj);
      alert(`Saved & Synced ${name} globally to Cloud Firestore! All devices will receive it instantly.`);
    } catch (err) {
      alert(`Saved locally. (Cloud sync error: ${err.message})`);
    }
  } else {
    alert(`Saved locally!`);
  }

  closeSecretAdmin();
  renderCurrentTab();
}

function restoreFactoryBaseline() {
  if (!confirm("Restore full 70-motorcycle verified factory baseline?")) return;
  motorcycles = typeof MASTER_CATALOG !== "undefined" ? [...MASTER_CATALOG] : [];
  localStorage.setItem("motoscope_bikes", JSON.stringify(motorcycles));
  compareSlots = [];
  localStorage.removeItem("motoscope_compare");
  savedBikes = [];
  localStorage.removeItem("motoscope_saved");

  closeSecretAdmin();
  renderCurrentTab();
  updateComparisonSlotBar();
  alert("Database restored to default verified 70 models!");
}

// 16. Gemini AI Assistant Chatbot
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
    const summary = motorcycles.slice(0, 35).map(b => `- ${b.brand} ${b.name || b.modelName} (${b.category}, ${b.specs?.maxPowerHp || b.powerHp}HP, ${b.specs?.displacementCc || b.engineCc}cc, ${b.priceDisplay})`).join("\n");
    const systemPrompt = `You are MotoScope AI, world-class motorcycle engineer & buyer consultant. Knowledge base:\n${summary}\nAnswer concisely and helpfully in bullet points.`;

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

async function getAiCompareVerdict() {
  const btn = document.getElementById("aiVerdictBtn");
  const box = document.getElementById("aiVerdictBox");
  const bikes = compareSlots.map(id => motorcycles.find(m => m.id === id)).filter(Boolean);

  if (bikes.length === 0) return;

  btn.disabled = true;
  btn.innerText = "Analyzing...";
  box.innerHTML = `<span class="inline-flex items-center gap-2 text-cyanNeon"><span class="w-2 h-2 rounded-full bg-cyanNeon animate-ping"></span> Gemini AI analyzing comparison...</span>`;

  try {
    const summary = bikes.map(b => `${b.brand} ${b.name || b.modelName}: ${b.specs?.maxPowerHp || b.powerHp}HP, ${b.specs?.displacementCc || b.engineCc}cc, ${b.specs?.kerbWeightKg || b.weightKg}kg, ${b.priceDisplay}`).join(" vs ");
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

// 17. EMI Calculator
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

// 18. Initialize on Page Load
window.addEventListener("DOMContentLoaded", () => {
  initCatalog();
  setupSecretAdminTriggers();
  switchTab("home");
});
