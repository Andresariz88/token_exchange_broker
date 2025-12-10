const fs = require("fs");
const { importSPKI, exportJWK } = require("jose");

(async () => {
    const publicKeyPem = fs.readFileSync("./public.pem", "utf8");

    // Importa la clave pública PEM a un objeto de clave real
    const publicKey = await importSPKI(publicKeyPem, "RS256");

    // Exporta en formato JWK correcto
    const jwk = await exportJWK(publicKey);

    // Añadimos metadata para JWKS
    jwk.kid = "ariza-key-1";
    jwk.use = "sig";
    jwk.alg = "RS256";

    fs.writeFileSync("jwks.json", JSON.stringify({ keys: [jwk] }, null, 2));

    console.log("JWKS generado correctamente.");
})();
