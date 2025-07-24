const AppError = require('../utils/AppError');
const videoService = require('../services/videoService.js');
const { ethers } = require("ethers");
require("dotenv").config();
const abi = require("../utils/contract_abi.json");

const provider = new ethers.JsonRpcProvider(process.env.INFURA_API_URL);
const wallet = new ethers.Wallet(process.env.PRIVATE_KEY, provider);
const contract = new ethers.Contract(process.env.CONTRACT_ADDRESS, abi, wallet);

async function getVideos(req, res, next) {
    try {
        const videos = await videoService.getVideos();
        res.status(200).json({
            status: 'success',
            videos,
        });
    } catch (error) {
        next(error);
    }
}

async function getBalance(req, res, next) {
    try {
        const balance = await contract.getBalance();
        res.status(200).json({
            balance: ethers.formatEther(balance) + " ETH"
        });
    } catch (err) {
        next(err);
    }
}

async function sendReward(req, res, next) {
    const { recipient } = req.body;

    if (!recipient || !ethers.isAddress(recipient)) {
        return next(new AppError("Valid recipient address required", 400));
    }

    try {
        const tx = await contract.sendReward(recipient);
        await tx.wait();
        res.status(200).json({
            message: "Reward sent!",
            txHash: tx.hash
        });
    } catch (err) {
        next(err);
    }
}

async function depositBalance(req, res, next) {
    const { amount } = req.body;

    if (!amount) {
        return next(new AppError("Amount in ETH required", 400));
    }

    try {
        const tx = await wallet.sendTransaction({
            to: process.env.CONTRACT_ADDRESS,
            value: ethers.parseEther(amount.toString())
        });
        await tx.wait();
        res.status(200).json({
            message: "Deposit successful",
            txHash: tx.hash
        });
    } catch (err) {
        next(err);
    }
}

module.exports = {
    getVideos,
    getBalance,
    sendReward,
    depositBalance
};
