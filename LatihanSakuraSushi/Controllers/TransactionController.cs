using LatihanSakuraSushi.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace LatihanSakuraSushi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class TransactionController(SakuraSushiContext _context) : ControllerBase
    {
        [HttpPost]
        [Authorize(Roles = "Waiter,Cashier")]
        public IActionResult NewTransaction([FromForm] string tableNumber = "")
        {
            var userId = User.FindFirstValue(ClaimTypes.Sid);
            var convertGuid = Guid.TryParse(userId, out Guid convertedGuid);

            // Validasi ketersediaan meja
            var checkTable = _context.Tables
                .FirstOrDefault(f => f.TableNumber == tableNumber);

            if (checkTable == null)
            {
                return NotFound(new { Message = "Table number not found!" });
            }

            var checkClosed = _context.Transactions.Any(f => f.ClosedAt == null && f.TableId == checkTable.Id);

            if (checkClosed)
            {
                return BadRequest(new { Message = "Table already has an open transaction" });
            }

            var newGuid = Guid.NewGuid();
            var uniqueCode = newGuid.ToString()[..4].ToUpper();

            if (_context.Transactions.Any(f => f.UniqueCode == uniqueCode))
            {
                uniqueCode = newGuid.ToString().Substring(1, 4).ToUpper();
            }

            Transaction newTransaction = new Transaction
            {
                Id = newGuid,
                TableId = checkTable.Id,
                CashierId = convertedGuid,
                UniqueCode = uniqueCode,
                OpenedAt = DateTime.UtcNow,
                TotalAmount = 0
            };

            _context.Transactions.Add(newTransaction);
            _context.SaveChanges();

            return Ok(uniqueCode);
        }
    }
}
