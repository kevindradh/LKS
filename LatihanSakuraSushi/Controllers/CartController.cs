using LatihanSakuraSushi.DTOs;
using LatihanSakuraSushi.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace LatihanSakuraSushi.Controllers
{
    [Route("api/Transaction")]
    [ApiController]
    public class CartController(SakuraSushiContext _context) : ControllerBase
    {
        [HttpPost("{uniqueCode}/Cart")]
        public IActionResult AddItemToCart(string uniqueCode, CartDTO cart)
        {
            var transaction = _context.Transactions
                .FirstOrDefault(f => f.UniqueCode == uniqueCode);

            if (transaction == null)
            {
                return NotFound(new { Message = "Transaction not found!" });
            }

            var checkItem = _context.Items
                .FirstOrDefault(f => f.Id == cart.ItemId);

            if (checkItem == null)
            {
                return NotFound(new { Message = "Item not found!" });
            }

            var sum = checkItem.Price * cart.Quantity;

            CartItem newCi = new CartItem()
            {
                Id = Guid.NewGuid(),
                TransactionId = transaction.Id,
                ItemId = checkItem.Id,
                Quantity = cart.Quantity,
                Price = checkItem.Price,
                TotalPrice = sum,
                AddedAt = DateTime.UtcNow
            };

            _context.CartItems.Add(newCi);
            _context.SaveChanges();

            var query = _context.CartItems
                .Where(f => f.TransactionId == transaction.Id)
                .Select(f => new
                {
                    f.Quantity,
                    f.TotalPrice,
                    f.AddedAt,
                    Item = new
                    {
                        f.Item.Id,
                        f.Item.Name,
                        f.Item.Description,
                        f.Item.Price,
                        f.Item.Available
                    }
                }).ToList();

            return Created("", query);
        }

        [HttpGet("{uniqueCode}/Cart")]
        public IActionResult ShowCart(string uniqueCode)
        {
            var transaction = _context.Transactions
                .FirstOrDefault(f => f.UniqueCode == uniqueCode);

            if (transaction == null)
            {
                return NotFound(new { Message = "Transaction not found!" });
            }

            var query = _context.CartItems
                .Where(f => f.TransactionId == transaction.Id)
                .Select(f => new
                {
                    f.Quantity,
                    f.TotalPrice,
                    f.AddedAt,
                    Item = new
                    {
                        f.Item.Id,
                        f.Item.Name,
                        f.Item.Description,
                        f.Item.Price,
                        f.Item.Available
                    }
                }).ToList();

            return Ok(query);
        }
    }
}
