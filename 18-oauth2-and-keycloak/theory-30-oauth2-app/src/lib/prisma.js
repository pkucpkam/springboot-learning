import { PrismaClient } from "@prisma/client";

const globalForPrisma = global;

export const prisma =
  globalForPrisma.prisma ??
  new PrismaClient({
    log: ["error", "warn"], // add "query" if need debug
  });

if (process.env.NODE_ENV !== "production") globalForPrisma.prisma = prisma;
