import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

async function main() {
  await prisma.customer.createMany({
    data: [
      { name: "Ty", email: "ty@gmail.com", address: "21K NVT" },
      { name: "Teo", email: "teo@gmail.com", address: "21K NVT" },
      { name: "To", email: "to@gmail.com", address: "21K NVT" },
      { name: "Bin", email: "bin@gmail.com", address: "21K NVT" },
      { name: "Bo", email: "bo@gmail.com", address: "21K NVT" },
    ],
    skipDuplicates: true, // prevent duplication error if run many times
  });
}

main()
  .then(() => console.log("Seeding finished."))
  .catch((e) => console.error(e))
  .finally(async () => {
    await prisma.$disconnect();
  });
