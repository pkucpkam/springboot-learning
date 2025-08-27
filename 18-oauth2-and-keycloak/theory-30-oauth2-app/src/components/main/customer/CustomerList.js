"use client";

import { useRouter } from "next/navigation";

export default function CustomerList({ customers }) {
  const router = useRouter();

  return (
    <table border="1" cellPadding="8">
      <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Email</th>
          <th>Address</th>
          <th>
            <button
              onClick={() => router.push(`/customers/add`)}
              className="text-blue-500 underline"
            >
              Thêm
            </button>
          </th>
        </tr>
      </thead>
      <tbody>
        {customers?.map((customer) => (
          <tr key={customer.id}>
            <td>{customer.id}</td>
            <td>{customer.name}</td>
            <td>{customer.email}</td>
            <td>{customer.address}</td>
            <td>
              <div className="flex gap-2 items-center">
                <button
                  onClick={() => router.push(`/customers/${customer.id}`)}
                  className="text-blue-500 underline"
                >
                  Xem
                </button>
                <button
                  onClick={() => router.push(`/customers/edit/${customer.id}`)}
                  className="text-green-500 underline"
                >
                  Sửa
                </button>
                <button className="text-red-500 underline">Xóa</button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
