// wwwroot/js/checkout.js

let zones = [];
let sousTotal = 0;
let fraisLivraison = 0;

function initCheckout(zonesData, totalPanier) {
    zones = zonesData;
    sousTotal = totalPanier;
    
    console.log('=== CHECKOUT INITIALIZED ===');
    console.log('Zones:', zones);
    console.log('Sous-total:', sousTotal);
}

function toggleLivraison() {
    const isLivraison = document.getElementById('livraison').checked;
    const section = document.getElementById('livraisonSection');
    const adresseInput = document.getElementById('adresse');
    
    console.log('Toggle livraison:', isLivraison);
    
    if (isLivraison) {
        section.style.display = 'block';
        adresseInput.required = true;
    } else {
        section.style.display = 'none';
        adresseInput.required = false;
        fraisLivraison = 0;
        updateTotal();
        document.getElementById('zoneInfo').classList.remove('active');
    }
}

function detectZone() {
    const adresseInput = document.getElementById('adresse');
    if (!adresseInput) {
        console.error('Adresse input not found!');
        return;
    }
    
    const adresse = adresseInput.value.toLowerCase().trim();
    console.log('=== DETECT ZONE ===');
    console.log('Adresse:', adresse);
    
    if (!adresse) {
        console.log('Adresse vide, return');
        return;
    }

    let zoneFound = null;

    // Chercher zone correspondante
    for (const zone of zones) {
        if (!zone.quartiers) {
            console.log('Zone sans quartiers:', zone.nom);
            continue;
        }
        
        const quartiers = zone.quartiers.toLowerCase().split(',');
        console.log(`Zone ${zone.nom}, quartiers:`, quartiers);
        
        for (const quartier of quartiers) {
            const quartierTrim = quartier.trim();
            const match = adresse.includes(quartierTrim);
            console.log(`  Test: "${adresse}" contient "${quartierTrim}"? ${match}`);
            
            if (match) {
                zoneFound = zone;
                console.log('  ✅ TROUVÉ!');
                break;
            }
        }
        if (zoneFound) break;
    }

    // Afficher zone trouvée
    if (zoneFound) {
        console.log('Zone détectée:', zoneFound);
        document.getElementById('zoneNom').textContent = zoneFound.nom;
        document.getElementById('zoneQuartiers').textContent = zoneFound.quartiers;
        document.getElementById('zonePrix').textContent = '+' + zoneFound.prixLivraison.toLocaleString() + ' FCFA';
        document.getElementById('zoneInfo').classList.add('active');
        
        fraisLivraison = zoneFound.prixLivraison;
        updateTotal();
    } else {
        console.log('❌ Aucune zone trouvée');
        document.getElementById('zoneInfo').classList.remove('active');
        fraisLivraison = 0;
        updateTotal();
    }
}

function updateTotal() {
    const total = sousTotal + fraisLivraison;
    console.log('Update total:', sousTotal, '+', fraisLivraison, '=', total);
    
    // Afficher/masquer ligne frais
    if (fraisLivraison > 0) {
        document.getElementById('fraisLivraisonLine').classList.remove('d-none');
        document.getElementById('fraisLivraison').textContent = '+' + fraisLivraison.toLocaleString() + ' FCFA';
    } else {
        document.getElementById('fraisLivraisonLine').classList.add('d-none');
    }
    
    // Mettre à jour totaux
    document.getElementById('totalFinal').textContent = total.toLocaleString() + ' FCFA';
    document.getElementById('totalDisplay').textContent = total.toLocaleString();
}